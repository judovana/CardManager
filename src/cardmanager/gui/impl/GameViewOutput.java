/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.gui.impl;

import cardmanager.gui.impl.drags.CardDrag;
import cardmanager.gui.ImageComponetn;
import cardmanager.gui.impl.drags.Drag;
import cardmanager.gui.impl.drags.MarkerDrag;
import cardmanager.impl.NetworkLogger;
import cardmanager.impl.Settings;
import cardmanager.impl.card.Card;
import cardmanager.impl.card.CardCache;
import cardmanager.impl.card.CardGeometry;
import cardmanager.impl.card.Marker;
import cardmanager.impl.card.MarkersCache;
import cardmanager.impl.packages.CardDefinition;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Jirka
 */
public class GameViewOutput extends JComponent {

    private static final String CARD_GEOMETRIE = "CARD_GEOMETRIE";
    private static final String MARKER_GEOMETRIE = "MARKER_GEOMETRIE";
    private static final String ADD_CARD = "ADD_CARD";
    private static final String ADD_MARKER = "ADD_MARKER";
    private static final String REMOVED_CARD = "REMOVED_CARD";
    private static final String REMOVED_MARKER = "REMOVED_MARKER";
    private static int MAX_NAMES;
    private final Random seed = new Random();
    Settings settings;
    NetworkLogger logger;
    List<Card> cards = Collections.synchronizedList(new ArrayList<Card>());
    List<Marker> markers = Collections.synchronizedList(new ArrayList<Marker>());
    private double totalRotation = 0;
    private double xMovement = 0;
    private double yMovement = 0;
    private boolean dirty = true;
    AffineTransform cachedSceneMarix;
    double zoom = 1;
    int xadd = 0;
    int yadd = 0;
    double[] drag1 = null; //rotation of scene
    CardDrag drag2 = null; //rotating cards
    CardDrag drag3 = null; //mooving cards
    MarkerDrag drag4 = null; //mooving markers
    MarkerDrag drag5 = null; //rotating markers
    //double[] drag4 =null; //creating selection
    Shape[] controlPoints;
    String[] cachedNames;
    BufferedImage[] namesImages;
    AffineTransform[][] namesPositions;
    private JPopupMenu lastpopup;
    private Integer holding;
    Runnable syncOut = new Runnable() {

        public void run() {
            Random r = new Random();
            while (true) {
                try {

                    settings.getTemp().getNetwork().syncTable(createSentence("ERROR", (Card) null));
                    Thread.sleep(300 + r.nextInt(300));

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    };
    Runnable scroller = new Runnable() {

        public void run() {
            while (true) {
                try {
                    xMovement += xadd;
                    yMovement += yadd;
                    dirty = true;
                    repaint();
                    Thread.sleep(100);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    };
    Runnable offer = new Runnable() {

        public void run() {
            while (true) {
                try {
                    java.awt.Point p = MouseInfo.getPointerInfo().getLocation();
                    if (self.isShowing()) {
                        java.awt.Point l = self.getLocationOnScreen();
                        if (p.x < l.x || p.y < l.y || p.x > l.x + self.getWidth() || p.y > l.y + self.getHeight()) {
                            xadd = 0;
                            yadd = 0;
                        }
                    } else {
                        xadd = 0;
                        yadd = 0;
                    }
                    Thread.sleep(50);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    };
    private GameViewOutput self;
    private double initialRotation;

    public void resetView() {
        totalRotation = initialRotation;
        zoom = 1;
        center(getParent());
        repaint();
    }

    public AffineTransform finishMatrix(BufferedImage i, AffineTransform sceneTransform, Card card) {
        return finishMatrix(i, sceneTransform, card.getCardGeometry(), settings.getCardWidth(), settings.getCardHeight());
    }

    public AffineTransform finishMatrix(BufferedImage i, AffineTransform sceneTransform, CardGeometry card, int w, int h) {
        int iw = settings.getCardWidth();
        int ih = settings.getCardHeight();
        if (i != null) {
            iw = i.getWidth();
            ih = i.getHeight();
        }
        double sx = (float) w / (float) iw;
        double sy = (float) h / (float) ih;
        return finishMatrix(sx, sy, sceneTransform, card, w, h);
    }

    public AffineTransform finishMatrix(double sx, double sy, AffineTransform sceneTransform, CardGeometry card, int w, int h) {
        AffineTransform cardScale = AffineTransform.getScaleInstance(sx, sy);
        AffineTransform at = new AffineTransform();
        at.concatenate(sceneTransform);
        at.concatenate(card.getArchRotatedMatrix(w / 2, h / 2));
        at.concatenate(card.getMovementMatrix());
        at.concatenate(cardScale);
        return at;
    }

    private void alignCardToHalfAPi(Card card) {
        double r = card.getCardGeometry().getR() + initialRotation;
        double angle = (Math.PI / 2d);
        long rr = Math.round(r / angle);
        card.getCardGeometry().setR(((double) rr) * angle - initialRotation);
        settings.getTemp().getNetwork().syncTable(createSentence(CARD_GEOMETRIE, card));
    }

    private void takeCardToHand(Card selected) {
        cards.remove(selected);
        settings.getTemp().getNetwork().messageToServerForAll(" teken card from table to hand - " + settings.getTemp().getPackage().getHand().size());
        settings.getTemp().getNetwork().syncTable(createSentence(REMOVED_CARD, selected));
    }

    private void tap(Card selected) {
        rotate(selected, Math.PI / 2);
    }

    private void untap(Card selected) {
        rotate(selected, -Math.PI / 2);
    }

    private void rotate(Card selected, double angle) {
        selected.getCardGeometry().setR(selected.getCardGeometry().getR() + angle);
        repaint();
        settings.getTemp().getNetwork().syncTable(createSentence(CARD_GEOMETRIE, selected));
    }

    public final void init() {
        self = this;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
                new KeyEventDispatcher() {

                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (lastpopup != null && lastpopup.isVisible()) {
                            for (int i = 0; i < lastpopup.getComponentCount(); i++) {
                                process((JMenuItem) (lastpopup.getComponent(i)), e);
                            }

                        }
                        if (e.getID() == KeyEvent.KEY_PRESSED) {
                            holding = e.getKeyCode();
                        }
                        if (e.getID() == KeyEvent.KEY_RELEASED) {
                            holding = null;
                        }
                        return false;
                    }

                    private void process(JMenuItem jMenuItem, KeyEvent e) {
                        if (jMenuItem instanceof JMenu) {
                            JMenu jMenuItemX = (JMenu) jMenuItem;
                            for (int i = 0; i < jMenuItemX.getMenuComponentCount(); i++) {
                                process((JMenuItem) (jMenuItemX.getMenuComponent(i)), e);
                            }
                        }
                        if (jMenuItem.getMnemonic() == e.getKeyCode()) {
                            ActionListener[] q = jMenuItem.getActionListeners();
                            for (ActionListener actionListener : q) {
                                actionListener.actionPerformed(null);
                            }
                        }
                    }
                });

        while (true) {

            if (cachedNames == null) {
                try {
                    Thread.sleep(new Random().nextInt(500));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                cachedNames = settings.getTemp().getNetwork().getPlayersInOrder();
                if (cachedNames != null) {
                    if (cachedNames.length >= 1) {
                        break;
                    }
                }
            }
        }
        Graphics2D initier = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics();
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < cachedNames.length; i++) {
            String name = cachedNames[i];

            int w = initier.getFontMetrics().stringWidth(name);
            if (w > max) {
                max = w;
            }
            //   int h = initier.getFontMetrics().getHeight();
        }
        MAX_NAMES = (2000 - max) / (max * 2);
        controlPoints = new Shape[cachedNames.length + 1];
        namesImages = new BufferedImage[cachedNames.length];
        namesPositions = new AffineTransform[cachedNames.length][MAX_NAMES];
        controlPoints[0] = new Rectangle2D.Float(-1, -1, 3, 3);

        for (int i = 0; i < cachedNames.length; i++) {
            String name = cachedNames[i];
            controlPoints[i + 1] = new Rectangle2D.Float(2000, 0, 3, 3);
            AffineTransform r = AffineTransform.getRotateInstance((6.28d * (double) i) / ((double) cachedNames.length));

            controlPoints[i + 1] = r.createTransformedShape(controlPoints[i + 1]);

            int w = initier.getFontMetrics().stringWidth(name);
            int h = initier.getFontMetrics().getHeight();
            for (int xx = 0; xx < MAX_NAMES; xx++) {
                namesPositions[i][xx] = new AffineTransform();
                namesPositions[i][xx].concatenate(r);
                namesPositions[i][xx].concatenate(AffineTransform.getTranslateInstance(xx * 2000 / MAX_NAMES, 0d));

            }
            namesImages[i] = new BufferedImage(w, 2 * h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = namesImages[i].createGraphics();
            g.setColor(Color.BLACK);
            g.drawString(name, 0, h);
//            try{
//            ImageIO.write(namesImages[i],"png" , new File(i+".png"));
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }


        }
        for (int i = 0; i < cachedNames.length; i++) {
            if (cachedNames[i].equals(settings.getName())) {
                totalRotation = -(6.28d * (double) i) / ((double) cachedNames.length);
                initialRotation = totalRotation;
                dirty = true;
                break;

            }
        }

        /*not working:(
        new Thread(syncOut).start();
         * programmed syncinc by single op. funguje faajn:)
         */
        new Thread(scroller).start();
        new Thread(offer).start();
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                if (getParent() != null) {
                    center(getParent());
                }
            }
        });
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                xadd = 0;
                yadd = 0;
            }
        });


        addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (holding != null) {
                    if (e.getButton() == e.BUTTON1) {
                        int i = holdingToNumKey(holding);
                        if (i >= 0 && i <= 9) {
                            Marker m = MarkersComponent.getMarker(i);
                            if (m != null) {
                                addMarker(m, getMarkersPoint(e.getX(), e.getY()));
                            }
                            return;
                        }
                    }

                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    Marker m = getMarker(e.getX(), e.getY());
                    if (m != null) {
                        markers.remove(m);
                        settings.getTemp().getNetwork().messageToServerForAll(" removed token");
                        settings.getTemp().getNetwork().syncTable(createSentence(REMOVED_MARKER, m));
                        repaint();
                        return;
                    }
                }
                final Card selected = getCard(e.getX(), e.getY());
                if (selected == null) {
                    return;
                }
                if (e.getClickCount() > 1) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        tap(selected);
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        untap(selected);
                    }
                } else {
                    JMenuItem[] jmi = new JMenuItem[23];
                    final JPopupMenu p = new JPopupMenu();
                    jmi[1] = new JMenuItem(selected.getDef().getId());
                    jmi[1].setEnabled(false);
                    jmi[2] = new JMenuItem("flip");
                    jmi[2].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            selected.setFace(!selected.isFace());
                            settings.getTemp().getNetwork().syncTable(createSentence(CARD_GEOMETRIE, selected));
                            repaint();
                        }
                    });

                    jmi[3] = new JMenuItem("take to your hand");
                    jmi[3].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            settings.getTemp().getPackage().addToHand(selected);
                            takeCardToHand(selected);
                            repaint();
                        }
                    });

                    final JMenu arange = new JMenu("arrange");
                    jmi[4] = new JMenuItem("bottom");
                    jmi[4].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            cards.remove(selected);
                            cards.add(0, selected);
                            repaint();
                        }
                    });
                    jmi[5] = new JMenuItem("top");
                    jmi[5].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            cards.remove(selected);
                            cards.add(selected);
                            repaint();
                        }
                    });


                    jmi[6] = new JMenuItem("down");
                    jmi[6].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            int i = cards.indexOf(selected);
                            cards.remove(selected);
                            i--;
                            if (i < 0) {
                                i = 0;
                            }
                            cards.add(i, selected);
                            repaint();
                        }
                    });
                    jmi[7] = new JMenuItem("up");
                    jmi[7].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            int i = cards.indexOf(selected);
                            cards.remove(selected);
                            i++;
                            if (i >= cards.size()) {
                                cards.add(selected);
                            } else {
                                cards.add(i, selected);
                            }
                            repaint();
                        }
                    });
                    jmi[8] = new JMenuItem("to half a pi");
                    jmi[8].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            alignCardToHalfAPi(selected);
                            repaint();
                        }
                    });

                    jmi[9] = new JMenuItem("view");
                    jmi[9].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            ImageComponetn.expand(CardCache.getInstance().getImage(selected.getDef().getId()), false, "", null);
                        }
                    });


                    p.add(jmi[1]);
                    p.add(jmi[2]);
                    p.add(jmi[3]);
                    p.add(arange);
                    arange.add(jmi[4]);
                    arange.add(jmi[5]);
                    arange.add(jmi[6]);
                    arange.add(jmi[7]);
                    arange.add(jmi[8]);
                    p.add(jmi[9]);
                    jmi[10] = new JMenuItem("tap (double Left click)");
                    jmi[10].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            tap(selected);
                        }
                    });
                    jmi[11] = new JMenuItem("untap (double right click)");
                    jmi[11].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            untap(selected);
                        }
                    });
                    p.add(jmi[10]);
                    p.add(jmi[11]);
                    Popupizer popupizer = new Popupizer();
                    int q = 1;
                    for (int i = 1; i < 10; i++) {
                        q++;
                        JMenuItem jMenuItem = jmi[q];
                        if (i == 4) {
                            jMenuItem = arange;
                            q--;
                        } else {
                        }

                        if (i != 4) {
                            popupizer.addAndInc(jMenuItem);
                        }

                    }
                    arange.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            arange.setPopupMenuVisible(true);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            if (!arange.isPopupMenuVisible()) {
                                arange.setPopupMenuVisible(false);
                            }
                        }
                    });

                    for (int i = 0; i < jmi.length; i++) {
                        JMenuItem jMenuItem = jmi[i];
                        if (jMenuItem == null) {
                            continue;
                        }
                        jMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                p.setVisible(false);
                            }
                        });

                    }

                    lastpopup = p;
                    p.show(null, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
                }



            }

            public void mousePressed(MouseEvent e) {
                Marker marker = getMarker(e.getX(), e.getY());
                Card selected = null;
                if (marker == null) {
                    selected = getCard(e.getX(), e.getY());
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (selected == null && marker == null) {
                        //rotate scene
                        drag1 = new double[3];
                        drag1[0] = (double) e.getX();
                        drag1[1] = (double) e.getY();
                        drag1[2] = totalRotation;
                    }
                    if (selected != null) {
                        //rotate card
                        drag2 = new CardDrag();
                        drag2.affected = selected;
                        drag2.old = new CardGeometry(selected.getCardGeometry());
                        drag2.x = e.getX();
                        drag2.y = e.getY();

                    }
                    if (marker != null) {
                        //rotate card
                        drag5 = new MarkerDrag();
                        drag5.affected = marker;
                        drag5.old = new CardGeometry(marker.getGeometry());
                        drag5.x = e.getX();
                        drag5.y = e.getY();

                    }
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (selected != null) {
                        drag3 = new CardDrag();
                        drag3.affected = selected;
                        drag3.old = new CardGeometry(selected.getCardGeometry());
                        drag3.x = e.getX();
                        drag3.y = e.getY();
                    }
                    if (marker != null) {
                        drag4 = new MarkerDrag();
                        drag4.affected = marker;
                        drag4.old = new CardGeometry(marker.getGeometry());
                        drag4.x = e.getX();
                        drag4.y = e.getY();
                    }
                }


            }

            public void mouseReleased(MouseEvent e) {
                if (drag2 != null) {
                    settings.getTemp().getNetwork().syncTable(createSentence(CARD_GEOMETRIE, drag2.affected));
                }
                if (drag5 != null) {
                    settings.getTemp().getNetwork().syncTable(createSentence(MARKER_GEOMETRIE, drag5.affected));
                }
                if (drag3 != null) {
                    settings.getTemp().getNetwork().syncTable(createSentence(CARD_GEOMETRIE, drag3.affected));
                }
                if (drag4 != null) {
                    settings.getTemp().getNetwork().syncTable(createSentence(MARKER_GEOMETRIE, drag4.affected));
                }
                drag1 = null;
                drag2 = null;
                drag3 = null;
                drag4 = null;
                drag5 = null;
            }

            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
                xadd = 0;
                yadd = 0;
            }
        });
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                l(e);

                if (drag1 != null) {
                    double deltaX = (e.getX() - drag1[0]);
                    totalRotation = drag1[2] + deltaX / 30;
                    dirty = true;
                    repaint();
                }
                if (drag2 != null || drag5 != null) {
                    Drag drag = null;
                    if (drag2 != null) {
                        drag = drag2;
                    }
                    if (drag5 != null) {
                        drag = drag5;
                    }
                    double deltaX = (e.getX() - drag.x);
                    drag.getAffectedsGeomethry().setR(drag.old.getR() + deltaX / 30d);
                    repaint();
                }
                if (drag3 != null || drag4 != null) {
                    Drag drag = null;
                    if (drag4 != null) {
                        drag = drag4;
                    }
                    if (drag3 != null) {
                        drag = drag3;
                    }
                    double[] line = new double[4];
                    line[0] = drag.x;
                    line[1] = drag.y;
                    line[2] = e.getX();
                    line[3] = e.getY();
                    double[] resultLine = new double[4];
                    Shape path = new Line2D.Double(line[0], line[1], line[2], line[3]);
                    AffineTransform at = getSceneTransform();
                    try {
                        at.invert();
                        Shape wanted = at.createTransformedShape(path);
                        PathIterator p = wanted.getPathIterator(new AffineTransform());
                        int i = 0;
                        while (!p.isDone()) {
                            double[] xy = new double[6];
                            int a = p.currentSegment(xy);
                            p.next();
                            resultLine[i] = xy[0];
                            resultLine[i + 1] = xy[1];
                            i += 2;
                        }
                        double deltaX = resultLine[2] - resultLine[0];
                        double deltaY = resultLine[3] - resultLine[1];
                        drag.getAffectedsGeomethry().setX((int) (drag.old.getX() + deltaX));
                        drag.getAffectedsGeomethry().setY((int) (drag.old.getY() + deltaY));
                        repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                l(e);
                if (lastpopup != null) {
                    lastpopup.setVisible(false);
                }
            }

            private void l(MouseEvent e) {
                xadd = 0;
                yadd = 0;
                if (e.getX() < 30) {
                    xadd = +10;
                }
                if (e.getY() < 30) {
                    yadd = +10;
                }
                if (e.getX() > getWidth() - 30) {
                    xadd = -10;
                }
                if (e.getY() > getHeight() - 30) {
                    yadd = -10;
                }
            }
        });
        addMouseWheelListener(new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent e) {
                double oldz = zoom;
                zoom += (double) e.getWheelRotation() / 4d;
                if (zoom <= 0) {
                    zoom = oldz;
                    return;
                }

                try {

                    double z1 = oldz;
                    double z2 = zoom;

                    Point2D.Double posun = new Point2D.Double(xMovement * z1, yMovement * z1);
                    double top = ((double) (e.getY() - posun.y)) / z1;
                    double left = ((double) (e.getX() - posun.x)) / z1;
                    Point2D.Double nwPosun = new Point2D.Double();
                    nwPosun.x = e.getX() - (int) (z2 * left);
                    nwPosun.y = e.getY() - (int) (z2 * top);
                    xMovement = nwPosun.x / z2;
                    yMovement = nwPosun.y / z2;

                    dirty = true;
                    repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }








            }
        });


    }

    public void center(Container parent) {
        xMovement = (parent.getWidth() / zoom) / 2;
        yMovement = (parent.getHeight() / zoom) / 2;
        dirty = true;
    }

    public GameViewOutput(Settings settings, NetworkLogger logger) {

        this.settings = settings;
        this.logger = logger;
        //get players names atd....
        init();
    }
//true= ok, false=face down

    public void add(Card card, boolean face) {
        addLocal(card, face);
        settings.getTemp().getNetwork().syncTable(createSentence(ADD_CARD, card));
    }

    public void addLocal(Card card, boolean face) {
        card.setCardGeometry(new CardGeometry(-settings.getCardWidth() / 2 + 10 - seed.nextInt(20), -settings.getCardHeight() / 2 + 10 - seed.nextInt(20), -totalRotation + 0.25 - seed.nextDouble() / 2d));
        card.setFace(face);
        cards.add(card);

        repaint();
    }

    public void addMarker(Marker card, Point p) {
        addLocalMarker(card, p);
        settings.getTemp().getNetwork().syncTable(createSentence(ADD_MARKER, card));
        settings.getTemp().getNetwork().messageToServerForAll(" added token");
    }

    public void addLocalMarker(Marker card, Point p) {
        if (p == null) {
            card.setGeometry(new CardGeometry(-settings.getCardWidth() / 2 + 10 - seed.nextInt(20), -settings.getCardHeight() / 2 + 10 - seed.nextInt(20), -totalRotation + 0.25 - seed.nextDouble() / 2d));
        } else {
            card.setGeometry(new CardGeometry(p.x, p.y, -totalRotation));
        }
        markers.add(card);

        repaint();
    }

    private AffineTransform getSceneTransform() {
        if (cachedSceneMarix != null && dirty == false) {
            return cachedSceneMarix;
        }

        AffineTransform deskRotate = AffineTransform.getRotateInstance(totalRotation, xMovement, yMovement);
        AffineTransform deskTranslate = AffineTransform.getTranslateInstance(xMovement, yMovement);
        AffineTransform deskScale = AffineTransform.getScaleInstance(zoom, zoom);


        cachedSceneMarix = new AffineTransform();
        cachedSceneMarix.concatenate(deskScale);
        cachedSceneMarix.concatenate(deskRotate);
        cachedSceneMarix.concatenate(deskTranslate);

        return cachedSceneMarix;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        AffineTransform sceneTransform = getSceneTransform();

        Shape center = sceneTransform.createTransformedShape(controlPoints[0]);
        g2d.draw(center);
        for (int i = 1; i < controlPoints.length; i++) {
            Shape t = sceneTransform.createTransformedShape(controlPoints[i]);
            g2d.drawLine((int) center.getBounds2D().getCenterX(), (int) center.getBounds2D().getCenterY(),
                    (int) t.getBounds2D().getCenterX(), (int) t.getBounds2D().getCenterY());
            g2d.draw(t);

        }
        for (int i = 0; i < namesImages.length; i++) {
            for (int xx = 0; xx < MAX_NAMES; xx++) {
                AffineTransform af = new AffineTransform();
                af.concatenate(sceneTransform);
                af.concatenate(namesPositions[i][xx]);
                g2d.drawImage(namesImages[i], af, null);
            }

        }
        g2d.setTransform(new AffineTransform());

        for (Iterator<Card> it = cards.iterator(); it.hasNext();) {
            Card card = it.next();
            BufferedImage i = null;
            if (card.isFace()) {
                CardDefinition def = card.getDef();
                String id = "totally_unknown_and_null";
                if (def != null) {
                    id = def.getId();
                }
                i = CardCache.getInstance().getImage(id);
            } else {
                i = CardCache.getInstance().getBackground(settings.getBackground());
            }
            AffineTransform at = finishMatrix(i, sceneTransform, card);
            g2d.drawImage(i, at, null);
            //  g2d.draw(r);
        }

        for (Iterator<Marker> it = markers.iterator(); it.hasNext();) {
            Marker marker = it.next();
            BufferedImage i = null;

            i = MarkersCache.getInstance(settings.getMarkerRadius()).getMarkerImage(marker.getColor(), marker.getTxt());

            AffineTransform at = finishMatrix(1, 1, sceneTransform, marker.getGeometry(), settings.getMarkerRadius(), settings.getMarkerRadius());
            g2d.drawImage(i, at, null);
            //  g2d.draw(r);
        }
        //g2d.drawLine(0, 0, xadd, yadd);
    }

    private Card getCard(int x, int y) {

        AffineTransform sceneTransform = getSceneTransform();
        for (int i = cards.size() - 1; i >= 0; i--) {
            Card card = cards.get(i);
            String id = "null_and_totally_unknown";
            CardDefinition def = card.getDef();
            if (def != null) {
                id = def.getId();
            }
            BufferedImage im = CardCache.getInstance().getImage(id);
            if (im == null) {
                return null;
            }
            AffineTransform at = finishMatrix(im, sceneTransform, card);
            Shape p = new Rectangle2D.Float(0, 0, im.getWidth(), im.getHeight());
            Shape r = at.createTransformedShape(p);

            if (r.contains(x, y)) {
                return card;
            }
        }
        return null;

    }

    private Marker getMarker(int x, int y) {

        AffineTransform sceneTransform = getSceneTransform();
        for (int i = markers.size() - 1; i >= 0; i--) {
            Marker card = markers.get(i);

            AffineTransform at = finishMatrix(1, 1, sceneTransform, card.getGeometry(), settings.getMarkerRadius(), settings.getMarkerRadius());
            int rr = settings.getMarkerRadius();
            Shape p = new Rectangle2D.Float(0, 0, rr, rr);
            Shape r = at.createTransformedShape(p);

            if (r.contains(x, y)) {
                return card;
            }
        }
        return null;

    }

    public synchronized String createSentence(String action, Card affected) {
        try {
            StringBuilder s = new StringBuilder();

            s.append(action).append("&").append(affected.getSentence());

            return s.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "UNKNOWN" + "&" + "what:here;";
        }
    }

    public synchronized String createSentence(String action, Marker affected) {
        try {
            StringBuilder s = new StringBuilder();

            s.append(action).append("&").append(affected.getSentence());

            return s.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "UNKNOWN" + "&" + "what:here;";
        }
    }

    synchronized public void remoteUpdate(String[] states, String action) {
        for (int i = 0; i < states.length; i++) {
            if (action.equalsIgnoreCase(CARD_GEOMETRIE) || action.equalsIgnoreCase(ADD_CARD) || action.equalsIgnoreCase(REMOVED_CARD)) {
                Card element = Card.cardFromSentence(states[i], settings.getTemp().getCollection());
                long id = element.getId();
                CardDefinition cardDef = element.getDef();
                int x = element.getCardGeometry().getX();
                int y = element.getCardGeometry().getY();
                double r = element.getCardGeometry().getR();
                boolean face = element.isFace();

                if (action.equalsIgnoreCase(CARD_GEOMETRIE) || action.equalsIgnoreCase(ADD_CARD)) {
                    boolean cardFound = false;
                    for (int j = 0; j < cards.size(); j++) {
                        Card card = cards.get(j);
                        if (card.getId() == id) {
                            card.getCardGeometry().setR(r);
                            card.getCardGeometry().setX(x);
                            card.getCardGeometry().setY(y);
                            card.setFace(face);
                            cardFound = true;
                            break;
                        }

                    }
                    if (cardFound) {
                        continue;
                    }
                    Card card = new Card(cardDef, "remote");
                    card.setId(id);
                    addLocal(card, face);
                    card.getCardGeometry().setR(r);
                    card.getCardGeometry().setX(x);
                    card.getCardGeometry().setY(y);
                }

                if (action.equalsIgnoreCase(REMOVED_CARD)) {

                    for (int j = 0; j < cards.size(); j++) {
                        Card card = cards.get(j);
                        if (card.getId() == id) {
                            cards.remove(j);
                            j--;
                        }

                    }

                }


            }

            if (action.equalsIgnoreCase(MARKER_GEOMETRIE) || action.equalsIgnoreCase(ADD_MARKER) || action.equalsIgnoreCase(REMOVED_MARKER)) {
                Marker element = Marker.markerFromSentence(states[i]);
                String id = element.getId();
                Color c = element.getColor();
                int x = element.getGeometry().getX();
                int y = element.getGeometry().getY();
                double r = element.getGeometry().getR();

                if (action.equalsIgnoreCase(MARKER_GEOMETRIE) || action.equalsIgnoreCase(ADD_MARKER)) {
                    boolean cardFound = false;
                    for (int j = 0; j < markers.size(); j++) {
                        Marker card = markers.get(j);
                        if (card.getId().equals(id)) {
                            card.getGeometry().setR(r);
                            card.getGeometry().setX(x);
                            card.getGeometry().setY(y);
                            card.setColor(c);
                            cardFound = true;
                            break;
                        }

                    }
                    if (cardFound) {
                        continue;
                    }
                    addLocalMarker(element, new Point(x, y));
                    element.getGeometry().setR(r);
                    element.getGeometry().setX(x);
                    element.getGeometry().setY(y);
                }
                if (action.equalsIgnoreCase(REMOVED_MARKER)) {

                    for (int j = 0; j < markers.size(); j++) {
                        Marker card = markers.get(j);
                        if (card.getId().equals(element.getId())) {
                            markers.remove(j);
                            j--;
                        }

                    }

                }
            }
        }
//delete unfounds? SOLUTION MISSING
        repaint();
    }

    public void allignByName(String name) {
        for (Iterator<Card> it = cards.iterator(); it.hasNext();) {
            Card card = it.next();
            if (card.getOwener().equals(name)) {
                alignCardToHalfAPi(card);
            }

        }
        repaint();
    }

    public Point getMarkersPoint(int x, int y) {
        try {
            AffineTransform sceneTransform = getSceneTransform();
            AffineTransform un = getSceneTransform().createInverse();


            Shape p = new Rectangle2D.Float(x, y, x, y);
            Shape r = un.createTransformedShape(p);

            PathIterator pi = r.getPathIterator(null);
            double[] d = new double[5];
            pi.currentSegment(d);
            return new Point((int) d[0] - settings.getMarkerRadius() / 2, (int) d[1] - settings.getMarkerRadius() / 2);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static int holdingToNumKey(Integer holding) {
        if (holding == null) {
            return -1;
        }
        int i = holding - KeyEvent.VK_0; //0 for 0, 1 for 1...9 for 9
        if (i < 0 || i > 9) {
            i = holding - KeyEvent.VK_NUMPAD0;
            //0 for 0, 1 for 1...9 for 9
        }
        return i;
    }

    public void tapAllByName(String name) {
        for (Iterator<Card> it = cards.iterator(); it.hasNext();) {
            Card card = it.next();
            if (card.getOwener().equals(name)) {
                tap(card);
                settings.getTemp().getNetwork().syncTable(createSentence(CARD_GEOMETRIE, card));
            }

        }
        repaint();
    }

    public void untapByName(String name) {
        for (Iterator<Card> it = cards.iterator(); it.hasNext();) {
            Card card = it.next();
            if (card.getOwener().equals(name)) {
                untap(card);
                settings.getTemp().getNetwork().syncTable(createSentence(CARD_GEOMETRIE, card));
            }

        }
        repaint();
    }

    public void allToHandByName(String name) {
        for (int i=0; i< cards.size(); i++) {
            Card selected = cards.get(i);
            if (selected.getOwener().equals(name)) {
                settings.getTemp().getPackage().addToHand(selected);
                takeCardToHand(selected);
                i--;
            }

        }
        repaint();
    }

    public void tapUntappedByName(String name) {
        for (Iterator<Card> it = cards.iterator(); it.hasNext();) {
            Card card = it.next();
            if (card.getOwener().equals(name)) {
                if (isUnTapped(card)) {
                    tap(card);
                    settings.getTemp().getNetwork().syncTable(createSentence(CARD_GEOMETRIE, card));
                }
            }

        }
        repaint();
    }

    public void untapTappedByName(String name) {
         for (Iterator<Card> it = cards.iterator(); it.hasNext();) {
            Card card = it.next();
            if (card.getOwener().equals(name)) {
                if (isTapped(card)) {
                    untap(card);
                    settings.getTemp().getNetwork().syncTable(createSentence(CARD_GEOMETRIE, card));
                }
            }

        }
        repaint();
    }

    private boolean isUnTapped(Card card) {
        double r = card.getCardGeometry().getR() + initialRotation;
        long rr=(Math.round(r*100d));
        long angle = Math.round(100d*(Math.PI));
        long mo=rr%angle;
        long C=40;
        System.out.println(mo);
         return  (mo<C &&  mo>-C) ||  (mo>-angle-C &&  mo<-angle+C);
    }

    private boolean isTapped(Card card) {
        double r = card.getCardGeometry().getR() + initialRotation;
        long rr=(Math.round(r*100d));
        long angle = Math.round(100d*(Math.PI));
        long ahalf=angle/2;
        long mo=rr%angle;
        long C=40;
        System.out.println(mo);
        return  (mo<ahalf+C &&  mo>ahalf-C) ||  (mo>-ahalf-C &&  mo<-ahalf+C);
    }
}

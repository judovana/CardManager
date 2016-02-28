/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.gui.impl;

import cardmanager.gui.MyColorChooserDialog;
import cardmanager.impl.card.Marker;
import cardmanager.impl.card.MarkersCache;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class MarkersComponent extends JPanel {

    private static MarkersComponent singleton;

    public static Marker getMarker(int i) {
        try {
            if (i >= singleton.colors.size()) {
                return null;
            }
            Color c = singleton.colors.get(i);
            String t = singleton.getSttingSafely(i);
            return new Marker(c, t);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    List<Color> colors = new ArrayList<Color>();
    List<String> strings = new ArrayList<String>();
    private final MarkersComponent self;
    private MyColorChooserDialog jc;
    private Integer holding;

    public MarkersComponent() {
        if (singleton != null) {
            throw new IllegalStateException("This is singleton due to programmers lazines");
        }
        self = this;
        MarkersComponent.singleton = self;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
                new KeyEventDispatcher() {

                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (e.getID() == KeyEvent.KEY_PRESSED) {
                            holding = e.getKeyCode();
                        }
                        if (e.getID() == KeyEvent.KEY_RELEASED) {
                            holding = null;
                        }
                        return false;
                    }
                });
        self.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (jc == null) {
                    jc = new MyColorChooserDialog(null, true);
                }
                Integer edit = null;
                if (holding != null) {
                    int i = GameViewOutput.holdingToNumKey(holding);
                    if (i >= 0 && i <= 9) {
                        edit = i;

                    }
                }
                if (e.getClickCount() > 1) {
                    if (edit == null || edit >= colors.size()) {
                        jc.setVisible(true, null, null);
                        Color c = jc.getColor();
                        String s = jc.getString();
                        if (c == null) {
                            return;
                        }
                        colors.add(c);
                        strings.add(s);
                    } else {
                        jc.setVisible(true, colors.get(edit), strings.get(edit));
                        Color c = jc.getColor();
                        String s = jc.getString();
                        if (c == null) {
                            return;
                        }
                        colors.set(edit, c);
                        strings.set(edit, s);
                    }
                    validate();
                    repaint();
                }

            }
        });


    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int sh = g2d.getFontMetrics().getHeight();
        int x = 0;
        int y = 0;
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.black);
        g2d.drawString("Doubleclick to add marker", 0, sh);
        g2d.drawString("Hold number of choice an L-click on table to add. R-click on it to remove", 0, 2 * sh);



        if (colors.size() <= 0) {

            return;
        }

        int r1 = Math.max(getWidth(), getHeight()) / colors.size();
        int r2 = Math.min(getWidth(), getHeight());
        int r = Math.min(r1, r2);

        int i = -1;
        for (Color color : colors) {
            i++;
            g2d.setColor(color);
            g2d.fillOval(x, y, r, r);
            g2d.setColor(MarkersCache.invert(color));
            String s = "" + i;
            int sw = g2d.getFontMetrics().stringWidth(s);
            int sw2 = g2d.getFontMetrics().stringWidth(getSttingSafely(i));
            g2d.drawString("" + i, x + r / 2 - sw / 2, y + r / 2);
            g2d.drawString(getSttingSafely(i), x + r / 2 - sw2 / 2, y + r / 2 + sh);
            x += r;
            if (x >= getWidth() - r + 1) {
                x = 0;
                y += r;
            }

        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        paint(g);
    }

    private String getSttingSafely(int i) {
        try {
            String s = strings.get(i);
            if (s == null) {
                return "";
            } else {
                return s;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}

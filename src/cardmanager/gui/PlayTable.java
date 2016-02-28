/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PlayTable.java
 *
 * Created on 19.10.2010, 13:28:07
 */
package cardmanager.gui;

import cardmanager.gui.impl.GameViewOutput;
import cardmanager.gui.impl.HandComponent;
import cardmanager.gui.impl.MarkersComponent;
import cardmanager.gui.impl.MemoWatcher;
import cardmanager.gui.impl.OfflineHelp;
import cardmanager.gui.impl.OnlineHelp;
import cardmanager.gui.impl.Popupizer;
import cardmanager.impl.Settings;
import cardmanager.impl.NetworkLogger;
import cardmanager.impl.CollectionOperator;
import cardmanager.networking.NetworkGame;
import cardmanager.impl.card.Card;
import cardmanager.impl.gui.CardInstanceViewForSelectComponent;
import cardmanager.networking.impl.AbstractNetworkGame;
import cardmanager.impl.packages.CardDefinition;
import cardmanager.impl.packages.Package;
import cardmanager.impl.packages.PackageDef;
import cardmanager.impl.packages.SharedPackage;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Jirka
 */
public class PlayTable extends javax.swing.JFrame {

    Popupizer popupizer;
    CollectionOperator collection;
    NetworkLogger logger;
    Settings settings = null;
    Observer watchLog;
    MarkersComponent markers = new MarkersComponent();
    private PlayTable self;
    private OnlineHelp help;
    private OfflineHelp help2;

    /** Creates new form PlayTable */
    public PlayTable() {
        self = this;
        initComponents();
        jPanel8.add(markers);
        jPanel8.validate();
        collection = new CollectionOperator(0);
        settings = new Settings(0);
        settings.getTemp().setCollection(collection);
        logger = new NetworkLogger(settings);
        jList1.setModel(new DefaultListModel());
        new JListScroller(jScrollPane1, settings, jList1);//new thread, just scrolling jList1, from settings is reading autoscroll
        watchLog = new Observer() {

            public void update(Observable o, Object arg) {
                ((DefaultListModel) (jList1.getModel())).addElement(arg);
            }
        };
        logger.addObserver(watchLog);
        settings.getTemp().setMemoWatcher(new MemoWatcher(jTextArea1, settings));

    }

    private class ParametrizedAL1 implements ActionListener {

        String pile;//needs parsing!!
        Package pckg;

        public ParametrizedAL1(String id, Package pckg) {
            this.pile = AbstractNetworkGame.getNameFromNameAndCount(id);
            this.pckg = pckg;
        }

        public void actionPerformed(ActionEvent e) {
            Card c = (pckg.getTopFrom(pile));
            if (c != null) {
                pckg.addToHand(c);
                logger.writeSharedMessage(" have taken card from " + pile + " to hand. Remained " + pckg.getPile(pile).size() + ", hand " + pckg.getHand().size());

            } else {
                String ss = null;
                List<Card> l = pckg.getPile(pile);
                if (l != null) {
                    ss = new Integer(l.size()).toString();
                }

                logger.writeSharedMessage(" have unsucesfully tryied to take card from " + pile + " to hand. Remained " + ss + ", hand " + pckg.getHand().size());
            }
        }
    }

    private class ParametrizedAL3 implements ActionListener {
//seelct card

        String pile;//needs parsing!!
        Package pckg;

        public ParametrizedAL3(String id, Package pckg) {
            this.pile = AbstractNetworkGame.getNameFromNameAndCount(id);
            this.pckg = pckg;
        }

        public void actionPerformed(ActionEvent e) {
            SelectcardDialog sd = new SelectcardDialog(self, true, pckg.getPile(pile));
            sd.getSelectComponent().setViewModality(true);
            sd.setVisible(true);
            Card q = sd.getSelected1();
            sd.dispose();
            Card c = (pckg.selectCard(q, pile));
            if (c != null) {
                pckg.addToHand(c);
                logger.writeSharedMessage(" have selected card from " + pile + " to hand. Remained " + pckg.getPile(pile).size() + ", hand " + pckg.getHand().size());

            } else {
                logger.writeSharedMessage(" have unsucesfully tryied to select card from " + pile + " to hand. Remained " + pckg.getPile(pile).size() + ", hand " + pckg.getHand().size());
            }
        }
    }

    private class ParametrizedAL4 implements ActionListener {
//shifle pile

        String pile;//needs parsing!!
        Package pckg;

        public ParametrizedAL4(String id, Package pckg) {
            this.pile = AbstractNetworkGame.getNameFromNameAndCount(id);
            this.pckg = pckg;
        }

        public void actionPerformed(ActionEvent e) {
            pckg.sufflePile(pile);
            settings.getTemp().getLogger().writeSharedMessage(" shuffled pile " + pile);

        }
    }

    private class ParametrizedAL2 implements ActionListener {

        String name;
        String pile;

        public ParametrizedAL2(String name, String pile) {
            this.name = name;
            this.pile = AbstractNetworkGame.getNameFromNameAndCount(pile);

        }

        public void actionPerformed(ActionEvent e) {
            Card[] c = settings.getTemp().getNetwork().askCards(settings.getName(), name, pile, NetworkGame.FROMTOP, null);
            if (c != null) {
                settings.getTemp().getPackage().addToHand(c[0]);
                logger.writeSharedMessage(" have taken card from " + name + "'s pile " + pile + " to hand. Hand " + settings.getTemp().getPackage().getHand().size());

            } else {
                logger.writeSharedMessage(" have unsucesfully tryied to take card from " + name + "'s pile " + pile + " to hand. Hand " + settings.getTemp().getPackage().getHand().size());
            }
        }
    }

    private class ParametrizedAL5 implements ActionListener {

        String name;
        String pile;

        public ParametrizedAL5(String name, String pile) {
            this.name = name;
            this.pile = AbstractNetworkGame.getNameFromNameAndCount(pile);

        }

        public void actionPerformed(ActionEvent e) {
            Card[] c = settings.getTemp().getNetwork().askCards(settings.getName(), name, pile, NetworkGame.LIST, null);
            if (c != null) {

                logger.writeSharedMessage(" is selecting card from " + name + "'s pile " + pile + ". Hand " + settings.getTemp().getPackage().getHand().size());
                SelectcardDialog scd = new SelectcardDialog(self, true, Arrays.asList(c));
                scd.setMultipleSellection(false);
                scd.setVisible(true);
                Card cd = scd.getSelected1();
                if (cd == null) {
                    logger.writeSharedMessage(" selected nothing from " + name + "'s pile " + pile + ". Hand " + settings.getTemp().getPackage().getHand().size());
                } else {
                    settings.getTemp().getPackage().addToHand(cd);
                    logger.writeSharedMessage(" have selected card from " + name + "'s pile " + pile + ". Hand " + settings.getTemp().getPackage().getHand().size());
                    settings.getTemp().getNetwork().askCards(settings.getName(), name, pile, NetworkGame.STEAL, cd.getSentence());
                }

            } else {
                logger.writeSharedMessage(" have unsucesfully tryied to select card from " + name + "'s pile " + pile + " to hand. Hand " + settings.getTemp().getPackage().getHand().size());
            }
        }
    }

    private JPopupMenu createYourCardMenu(Settings settings) {
        final Package pckg = settings.getTemp().getPackage();
        if (popupizer == null) {
            popupizer = new Popupizer();
        }
        JPopupMenu jp = new JPopupMenu("Add cards to hand");
        JMenu topOfYourPiles = new JMenu("add card from top of your :");
        if (topOfYourPiles == null) {
            return jp;
        }
        jp.add(topOfYourPiles);
        popupizer.addAndInc(topOfYourPiles);
        for (String s : pckg.getPilesNamesWithCount()) {
            JMenuItem jmi = new JMenuItem(s);
            jmi.addActionListener(new ParametrizedAL1(s, pckg));


            topOfYourPiles.add(jmi);
            popupizer.addAndInc(jmi);
        }

        topOfYourPiles = new JMenu("select card from  your :");
        jp.add(topOfYourPiles);
        popupizer.addAndInc(topOfYourPiles);
        for (String s : pckg.getPilesNamesWithCount()) {
            JMenuItem jmi = new JMenuItem(s);
            jmi.addActionListener(new ParametrizedAL3(s, pckg));


            topOfYourPiles.add(jmi);
            popupizer.addAndInc(jmi);
        }

        topOfYourPiles = new JMenu("shufle cards in  your :");
        jp.add(topOfYourPiles);

        for (String s : pckg.getPilesNamesWithCount()) {
            JMenuItem jmi = new JMenuItem(s);
            jmi.addActionListener(new ParametrizedAL4(s, pckg));


            topOfYourPiles.add(jmi);
        }

        return jp;

    }

    private JPopupMenu createOthersCardMenu(Settings settings) {
        if (settings.getSharedPile()) {
            return new JPopupMenu("remote ops not allowed with shared pile");
        }
        if (popupizer == null) {
            popupizer = new Popupizer();
        }
        JPopupMenu jp = new JPopupMenu("Add cards to hand");

        String[] plrs = settings.getTemp().getNetwork().getPlayersExceptMe();
        if (plrs != null) {
            for (int i = 0; i < plrs.length; i++) {
                String string = plrs[i];

                JMenu topOfYourHis1 = new JMenu("add card from top of " + string + "'s pile:");
                jp.add(topOfYourHis1);
                popupizer.addAndInc(topOfYourHis1);
                String[] recieved = settings.getTemp().getNetwork().getPlayerPiles(string);

                if (recieved != null) {
                    for (String s : recieved) {
                        JMenuItem jmi = new JMenuItem(s);
                        jmi.addActionListener(new ParametrizedAL2(string, s));


                        topOfYourHis1.add(jmi);
                        popupizer.addAndInc(jmi);
                    }
                }


                JMenu topOfYourHis = new JMenu("select card from  " + string + "'s pile:");
                jp.add(topOfYourHis);
                popupizer.addAndInc(topOfYourHis);

                if (recieved != null) {
                    for (String s : recieved) {
                        JMenuItem jmi = new JMenuItem(s);
                        jmi.addActionListener(new ParametrizedAL5(string, s));


                        topOfYourHis.add(jmi);
                        popupizer.addAndInc(jmi);
                    }
                }
            }
        }
        return jp;
    }

    private JPopupMenu createGetCardMenu(Settings settings) {
        JPopupMenu jp = new JPopupMenu("Add cards to hand");
        popupizer = new Popupizer();
        jp.add(createYourCardMenu(settings));
        if (settings.getSharedPile() == false) {
            jp.add(createOthersCardMenu(settings));
        }
        return jp;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
    //     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        handControls = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tableControls = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        tablePanel = new javax.swing.JPanel();
        gameControlPanel = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jSplitPane5 = new javax.swing.JSplitPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane2.setDividerLocation(200);

        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        handControls.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        handControls.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                handControlsMousePressed(evt);
            }
        });

        jLabel1.setText("Hand:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel5.setText("    your ops");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel5MousePressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel6.setText("    others ops");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel6MousePressed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton1.setText("from collection");
        jButton1.setBorderPainted(false);
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout handControlsLayout = new javax.swing.GroupLayout(handControls);
        handControls.setLayout(handControlsLayout);
        handControlsLayout.setHorizontalGroup(
            handControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(handControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(handControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                .addContainerGap())
        );
        handControlsLayout.setVerticalGroup(
            handControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(handControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel4.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(handControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(handControls, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jSplitPane1.setTopComponent(jPanel1);

        tableControls.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Table");

        jButton2.setText("Align by name");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("reset view");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetView(evt);
            }
        });

        jButton4.setText("Tap all by name");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("unTap all by name");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("all to hand by name");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Tap all untaped bn");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("unTap all taped bn");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tableControlsLayout = new javax.swing.GroupLayout(tableControls);
        tableControls.setLayout(tableControlsLayout);
        tableControlsLayout.setHorizontalGroup(
            tableControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tableControlsLayout.setVerticalGroup(
            tableControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        tablePanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                tablePanelComponentResized(evt);
            }
        });
        tablePanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tableControls, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tablePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel2);

        jSplitPane2.setRightComponent(jSplitPane1);

        jSplitPane3.setDividerLocation(150);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jLabel3.setText("Game log:");

        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                    .addComponent(jLabel3))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane3.setTopComponent(jPanel3);

        jSplitPane4.setDividerLocation(150);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jLabel4.setText("Shared text field");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                    .addComponent(jLabel4))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane4.setTopComponent(jPanel5);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 163, Short.MAX_VALUE)
        );

        jSplitPane5.setLeftComponent(jPanel7);

        jPanel8.setLayout(new java.awt.BorderLayout());
        jSplitPane5.setRightComponent(jPanel8);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSplitPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 163, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
        );

        jSplitPane4.setRightComponent(jPanel6);

        jSplitPane3.setRightComponent(jSplitPane4);

        javax.swing.GroupLayout gameControlPanelLayout = new javax.swing.GroupLayout(gameControlPanel);
        gameControlPanel.setLayout(gameControlPanelLayout);
        gameControlPanelLayout.setHorizontalGroup(
            gameControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        gameControlPanelLayout.setVerticalGroup(
            gameControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );

        jSplitPane2.setLeftComponent(gameControlPanel);

        jMenu1.setText("Collection");

        jMenuItem1.setText("view");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Settings");

        jMenuItem2.setText("global settings");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem3.setText("network game settings");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("design package");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem7.setText("close all games and collections");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAll(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");

        jMenuItem5.setText("cardmanager.wz.cz");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showHelp(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuItem6.setText("some plain text:)");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showOflineHelp(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        logger.writeSharedMessage(" is looking to read only collection");
        new CollectionView(this, false, collection).setVisible(true);

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        String name = settings.getName();
        GlobalSettingsDialog g = new GlobalSettingsDialog(this, true, settings);
        g.setLocationRelativeTo(this);
        g.setVisible(true);
        if (!name.equals(settings.getName())) {
            logger.renamedPlayer(name, settings.getName());
        }

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        CreatePackageDialog g = new CreatePackageDialog(this, true, collection);
        g.setLocationRelativeTo(this);
        g.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        NetworkGameSettings g = new NetworkGameSettings(this, true, settings, collection, logger);
        g.setLocationRelativeTo(this);
        g.setVisible(true);
        if (g.getResult() != null) {
            logger.setNetwork(g.getResult());
            if (!settings.getSharedPile()) {
                g.getResult().messageToServerForAll(settings.getTemp().getBattlePackage().verify().saySentence());
                settings.getTemp().setPackage(cardmanager.impl.packages.PackageDef.unpackPackageDef(settings, settings.getTemp().getBattlePackage()));

            } else {
                settings.getTemp().setPackage(null);

                if (settings.getImServer()) {
                    //send shuffled package?
                    Package p = cardmanager.impl.packages.PackageDef.unpackPackageDef(settings, settings.getTemp().getBattlePackage());
                    settings.getTemp().setPackage(p);
                }
                SharedPackage p = new SharedPackage(settings);
                p.setServersPile((PackageDef) settings.getTemp().getPackage());
                settings.getTemp().setPackage(p);
            }
            jPanel4.removeAll();
            jPanel4.add(new HandComponent(settings));
            jPanel4.validate();
            jPanel4.repaint();

            tablePanel.removeAll();
            GameViewOutput gvo = new GameViewOutput(settings, logger);
            tablePanel.add(gvo);
            tablePanel.validate();
            gvo.center(tablePanel);
            settings.getTemp().setGameViewOutput(gvo);
            ///new newtwork game started
        }
        g.dispose();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void handControlsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_handControlsMousePressed
        // TODO add your handling code here:
        if (settings.getTemp().getPackage() != null) {
            JPopupMenu jp = createGetCardMenu(settings);


            jp.show((Component) evt.getSource(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_handControlsMousePressed

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MousePressed
        popupizer = new Popupizer();
        if (settings.getTemp().getPackage() != null) {
            JPopupMenu jp = createYourCardMenu(settings);


            jp.show((Component) evt.getSource(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jLabel5MousePressed

    private void jLabel6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MousePressed
        popupizer = new Popupizer();
        if (settings.getTemp().getPackage() != null) {
            JPopupMenu jp = new JPopupMenu("Not alowed");
            if (settings.getSharedPile() == false) {
                jp = createOthersCardMenu(settings);
            }


            jp.show((Component) evt.getSource(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jLabel6MousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here
        if (settings.getTemp().getPackage() != null) {
            CollectionView c = new CollectionView(self, true, collection);
            c.setMultipleSellection(false);
            c.getViewComponent().setViewModality(true);
            c.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            c.setVisible(true);
            Object[] o = c.getSelected();
            c.dispose();
            CardDefinition cd = null;
            if (o.length > 0) {
                CardInstanceViewForSelectComponent oo = (CardInstanceViewForSelectComponent) o[0];
                cd = oo.getCard();
            }

            if (cd != null) {
                settings.getTemp().getPackage().addToHand(new Card(cd, settings.getName()));
                logger.writeSharedMessage(" have selected card from collection to hand. hand " + settings.getTemp().getPackage().getHand().size());



            } else {
                logger.writeSharedMessage(" have unsucesfully tryied to select card from collection to hand. hand " + settings.getTemp().getPackage().getHand().size());
            }

        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void tablePanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tablePanelComponentResized
//        // TODO add your handling code here:
//        Component[] c=getComponents();
//        for (int i = 0; i < c.length; i++) {
//            Component component = c[i];
//            ComponentListener cl[]=component.getComponentListeners();
//            for (int j = 0; j < cl.length; j++) {
//                ComponentListener componentListener = cl[j];
//                componentListener.componentResized(evt);
//
//            }
//
//        }
    }//GEN-LAST:event_tablePanelComponentResized

    private void showHelp(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showHelp
        // TODO add your handling code here:
        if (help == null) {
            help = new OnlineHelp(self, false);
        }
        help.setVisible(true);

    }//GEN-LAST:event_showHelp

    private void showOflineHelp(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showOflineHelp
        if (help2 == null) {
            help2 = new OfflineHelp(self, false);
        }
        help2.setVisible(true);
    }//GEN-LAST:event_showOflineHelp

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        GameViewOutput gvo = settings.getTemp().getGameViewOutput();
        if (gvo != null) {
            gvo.allignByName(settings.getName());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void resetView(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetView
        // TODO add your handling code here:
        GameViewOutput gvo = settings.getTemp().getGameViewOutput();
        if (gvo != null) {
            gvo.resetView();
        }
    }//GEN-LAST:event_resetView

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        GameViewOutput gvo = settings.getTemp().getGameViewOutput();
        if (gvo != null) {
            gvo.tapAllByName(settings.getName());
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        GameViewOutput gvo = settings.getTemp().getGameViewOutput();
        if (gvo != null) {
            gvo.untapByName(settings.getName());
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        GameViewOutput gvo = settings.getTemp().getGameViewOutput();
        if (gvo != null) {
            gvo.allToHandByName(settings.getName());
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        GameViewOutput gvo = settings.getTemp().getGameViewOutput();
        if (gvo != null) {
            gvo.tapUntappedByName(settings.getName());
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        GameViewOutput gvo = settings.getTemp().getGameViewOutput();
        if (gvo != null) {
            gvo.untapTappedByName(settings.getName());
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void closeAll(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAll
        // TODO add your handling code here:
        try{
        if (settings.getTemp() != null) {
            try {
                settings.getTemp().getNetwork().closeAll();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (settings.getTemp().getServer() != null) {
                    settings.getTemp().getServer().closeAll();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                jPanel4.removeAll();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                tablePanel.removeAll();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                settings.clearTemp();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                settings.getTemp().setLoger(logger);
                settings.getTemp().setMemoWatcher(new MemoWatcher(jTextArea1, settings));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        }finally{
            jPanel4.validate();
            jPanel4.repaint();
            tablePanel.validate();
            tablePanel.repaint();
        }
    }//GEN-LAST:event_closeAll

        /**
         * @param args the command line arguments
         */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new PlayTable().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel gameControlPanel;
    private javax.swing.JPanel handControls;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JSplitPane jSplitPane5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel tableControls;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration//GEN-END:variables
}

/*
 *sever - chci karty od client1
 * client1 karty od klient 1
 *
 * client1 chci karty od klient2
 * server kiente2, dej krty oro klient 1
 * klint 2- karty od klient 2 pro klient 1
 * server - klientw 1 tu je response od klient 2
 *
 */

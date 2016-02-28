/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NetworkGameSettings.java
 *
 * Created on 24.10.2010, 9:15:13
 */
package cardmanager.gui;

import cardmanager.TreeTest;
import cardmanager.impl.CollectionOperator;
import cardmanager.networking.NetworkGame;
import cardmanager.impl.NetworkLogger;
import cardmanager.impl.packages.PackageDeffinition;
import cardmanager.impl.packages.PackageDeffinitionAndErrors;
import cardmanager.impl.Settings;
import cardmanager.impl.networking.JoinedPlayer;
import cardmanager.impl.networking.NetworkStarter;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Jirka
 */
public class NetworkGameSettings extends javax.swing.JDialog {

    private ViewPackagePanel curView;
    private CollectionOperator c;
    private Settings settings;
    private NetworkLogger logger;
    private NetworkGame result = null;

    public NetworkGame getResult() {
        return result;
    }

    public void setResult(NetworkGame result) {
        this.result = result;
    }

    /** Creates new form NetworkGameSettings */
    public NetworkGameSettings(java.awt.Frame parent, boolean modal, Settings ts, CollectionOperator c, NetworkLogger logger) {
        super(parent, modal);
        this.c = c;
        this.settings = ts;
        this.logger = logger;
        initComponents();

        jSpinner1.setModel(new SpinnerNumberModel(settings.getLastPort().intValue(), 0, Integer.MAX_VALUE, 1));

        jSpinner1.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                settings.setLastPort(((Number) jSpinner1.getValue()).intValue());
            }
        });


        jCheckBox2.setSelected(settings.getSharedPile());

        jTextField1.setText(settings.getLastIp());
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                actionPerformed(null);
            }

            public void removeUpdate(DocumentEvent e) {
                actionPerformed(null);
            }

            public void changedUpdate(DocumentEvent e) {
                actionPerformed(null);
            }

            public void actionPerformed(ActionEvent e) {
                settings.setLastIp(jTextField1.getText());
            }
        });


        jTree1.setModel(TreeTest.createPkgsModel(this, PackageDeffinition.ROOT, c));
        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree1.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
                jPanel3.removeAll();
                if (node == null) //Nothing is selected.
                {
                    curView = null;
                    jPanel3.validate();
                    jPanel3.repaint();
                    return;
                }

                if (node.isLeaf()) {
                    PackageDeffinition nodeInfo = (PackageDeffinition) node.getUserObject();
                    settings.getTemp().setBattlePackage(nodeInfo);
                    curView = new ViewPackagePanel(nodeInfo);
                    if (curView != null) {
                        curView.getView().setFilterAlowed(false);
                        jPanel3.add(curView);
                        curView.getView().setMultipleSellection(true);
                    }
                } else {
                    curView = null;
                    jPanel3.validate();
                    jPanel3.repaint();
                    return;
                }
                jPanel3.repaint();
                jPanel3.validate();
            }
        });

        jCheckBox2.setSelected(settings.getSharedPile());
        jCheckBox2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                settings.setSharedPile(jCheckBox2.isSelected());
            }
        });

        jCheckBox1.setSelected(settings.getImServer());
        jCheckBox1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                settings.setImServer(jCheckBox1.isSelected());
                jTextField1.setEnabled(!jCheckBox1.isSelected());
            }
        });

        jTextField1.setEnabled(!jCheckBox1.isSelected());

        pack();

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();

        jButton1.setText("Start game");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jSplitPane2.setDividerLocation(210);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane1.setDividerLocation(190);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jLabel2.setText("Content:");

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setTopComponent(jPanel1);

        jLabel3.setText("Netork game settings");

        jLabel4.setText("ip:");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel5.setText("port:");

        jCheckBox1.setText("or I'm an server");

        jCheckBox2.setText("shared pile");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("select random package from selected dir");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jCheckBox2))
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox1)))
                .addContainerGap(287, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(325, Short.MAX_VALUE)
                .addComponent(jCheckBox3)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addComponent(jCheckBox3))
        );

        jSplitPane1.setRightComponent(jPanel2);

        jSplitPane2.setBottomComponent(jSplitPane1);

        jLabel1.setText("Battle package:");

        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane2.setLeftComponent(jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    public void setRandomPackage(JTree tree) {
        DefaultMutableTreeNode selected ;
        if (jTree1.getLastSelectedPathComponent()==null){
         selected=(DefaultMutableTreeNode) jTree1.getModel().getRoot();
        }else{
        selected = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
        }
        if (selected.isLeaf()) {
            selected = (DefaultMutableTreeNode) selected.getParent();
        }
        Random r = new Random();
        while (true) {
            if (selected.isLeaf()) {
                break;
            }
            if (selected.getChildCount() == 0) {
                break;
            }
            int i = r.nextInt(selected.getChildCount());
            selected = (DefaultMutableTreeNode) selected.getChildAt(i);
        }
        tree.setSelectionPath(new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(selected)));



    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
                if (jCheckBox3.isSelected()) {
            setRandomPackage(jTree1);
        }
        if (jTree1.getSelectionCount() != 1 || !((DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent()).isLeaf()) {
            JOptionPane.showMessageDialog(this, "No selected package!");
            return;
        }
        if (settings.getTemp().getBattlePackage() == null) {
            JOptionPane.showMessageDialog(this, "No package!");
            return;
        }
        ProbeDialog p = new ProbeDialog(this, true);
        p.setLocationRelativeTo(this);
        NetworkStarter ns = new NetworkStarter(settings);
        settings.getTemp().setServer(ns.getServer());
        try {
            ns.start(p.getJList1(), p.getJList1(), p, p);
            if (settings.getImServer()) {
                boolean ok = p.ALES_OKK_AND_CONFIRMED_BY_SERVER;
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "Something wired occured");
                }

                result = ns.notifyFinished();
                this.setVisible(false);
            } else {
                result = ns.notifiedFinished();
                this.setVisible(false);
            }

        } finally {
            p.dispose();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
        settings.setSharedPile(jCheckBox2.isSelected());
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    class ProbeDialog extends JDialog {

        private JButton jButton2 = new JButton("everybody connected. Start game");
        private JButton rButton = new JButton("X");
        private JList jList1 = new JList(new DefaultListModel());
        private JScrollPane jScrollPane1 = new JScrollPane(jList1);
        public boolean ALES_OKK_AND_CONFIRMED_BY_SERVER = false;
        private ProbeDialog self;

        public ProbeDialog(Dialog parent, boolean modal) {
            super(parent, modal);
            self = this;
            init();
            setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            this.setSize(400, 400);
            pack();
            if (!settings.getImServer()) {
                this.jButton2.setEnabled(false);
                this.jButton2.setText("Conectig to server and waiting to it's commands");
            }

            rButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    System.exit(-1);
                }
            });

            jButton2.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ALES_OKK_AND_CONFIRMED_BY_SERVER = true;
                    self.setVisible(false);
                }
            });


        }

        public DefaultListModel getJList1() {
            return (DefaultListModel) jList1.getModel();
        }

        private void init() {
            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(rButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(rButton).addComponent(jButton2)).addContainerGap()));



        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Settings sss = new Settings(0);
                NetworkGameSettings dialog = new NetworkGameSettings(new javax.swing.JFrame(), true, sss, new CollectionOperator(0), new NetworkLogger(sss));
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CollectionView.java
 *
 * Created on 19.10.2010, 13:58:55
 */
package cardmanager.gui;

import cardmanager.FileBearer;
import cardmanager.impl.packages.CardDefinition;
import cardmanager.impl.gui.CardInstanceViewForSelectComponent;
import cardmanager.impl.CollectionOperator;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractListModel;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Jirka
 */
public class SelectCardComponent extends javax.swing.JSplitPane {

    private List<FileBearer> origCollection;
    private boolean viewModality;
    private boolean filterAlowed = true;
    private boolean previewAloved = true;
    private static String last=null;

    public boolean isPreviewAlowed() {
        return previewAloved;
    }

    public void setPreviewAlowed(boolean preview) {
        this.previewAloved = preview;
    }

    public boolean getFilterAlowed() {
        return filterAlowed;
    }

    public void setFilterAlowed(boolean filterAlowed) {
        this.filterAlowed = filterAlowed;
    }

    public void setViewModality(boolean viewModality) {
        this.viewModality = viewModality;
    }

    public boolean getViewModality() {
        return viewModality;
    }

    /** Creates new form CollectionView */
    public SelectCardComponent(List<FileBearer> collection) {
        super();
        jSplitPane1 = this;
        initComponents();
        this.origCollection = collection;
        jList1.setModel(new ListModelImpl(collection));
        

        jList1.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON3 && filterAlowed) {
                    String s;
                    if (last==null){
                      s = JOptionPane.showInputDialog("filter: use spaces to separate entries \n or use r: and enter regex or R: to case not sensitive regex");
                    }else{
                      s = JOptionPane.showInputDialog("filter: use spaces to separate entries \n or use r: and enter regex or R: to case not sensitive regex",last);
                    }
                    if (s == null) {
                        return;
                    }
                    last=s;
                    jList1.setModel(new ListModelImpl(origCollection, s));
                }
            }
        });



        jList1.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!previewAloved) {
                    return;
                }
                Object[] o = jList1.getSelectedValues();
                jPanel2.removeAll();
                int x = (int) Math.ceil(Math.sqrt(o.length));
                if (x <= 0) {
                    x = 1;
                }
                int y = o.length / x;
                jPanel2.setLayout(new GridLayout(x, y));
                if (o != null) {
                    for (Object object : o) {
                        FileBearer s = (FileBearer) object;
                        try {
                            ImageComponetn ni = new ImageComponetn(s.getFile());
                            ni.setViewModality(viewModality);
                            jPanel2.add(ni);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }
                jPanel2.validate();

            }
        });
        this.setDividerLocation(200);
    }

    public JList getList() {
        return jList1;
    }

    private void initComponents() {


        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();



        jSplitPane1.setDividerLocation(180);

        jList1.setFont(new java.awt.Font("Courier New", 0, 11));
        jScrollPane1.setViewportView(jList1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)));

        jSplitPane1.setRightComponent(jPanel1);


    }// </editor-fold>
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration

    private static class ListModelImpl extends AbstractListModel {

        private List<FileBearer> c;

        public ListModelImpl(List<FileBearer> c) {
            this.c = c;
        }

        private ListModelImpl(List<FileBearer> origCollection, String s) {
            List<FileBearer> r = new ArrayList<FileBearer>(origCollection.size());
            if (s.startsWith("r:")) {
                String regex = s.substring(2);
                for (FileBearer fileBearer : origCollection) {
                    String us = fileBearer.toString();
                    {
                        if (us.matches(regex)) {
                            r.add(fileBearer);
                        }

                    }
                }
            } else if (s.startsWith("R:")) {
                String regex = s.substring(2).toUpperCase();
                for (FileBearer fileBearer : origCollection) {
                    String us = fileBearer.toString().toUpperCase();
                    {
                        if (us.matches(regex)) {
                            r.add(fileBearer);
                        }

                    }
                }
            } else {
                String[] ss = s.toUpperCase().split(" ");
                for (FileBearer fileBearer : origCollection) {
                    String us = fileBearer.toString().toUpperCase();
                    for (int i = 0; i < ss.length; i++) {
                        if (us.contains(ss[i])) {
                            r.add(fileBearer);
                            break;
                        }

                    }
                }
            }
            c = r;
        }

        public int getSize() {
            return c.size();
        }

        public Object getElementAt(int index) {
            return (c.get(index));
        }
    }

    public void setMultipleSellection(boolean b) {
        if (!b) {
            jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            jList1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

    }

    public Object[] getSelected() {
        return jList1.getSelectedValues();
    }
}

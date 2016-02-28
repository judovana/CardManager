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

import cardmanager.impl.CollectionOperator;
import java.io.File;


/**
 *
 * @author Jirka
 */
public class CollectionView extends javax.swing.JDialog {
SelectCardComponent sc;
    /** Creates new form CollectionView */
    public CollectionView(java.awt.Frame parent, boolean modal,CollectionOperator collection) {
        super(parent, modal);
        initComponents();
        sc=new SelectCardComponent(collection.getCollectionForSelectingView());
        this.add(sc);
        this.setSize(800, 600);
      
    }

public Object[] getSelected(){
    return sc.getSelected();
}

    public SelectCardComponent getViewComponent() {
        return sc;
    }


    public void setMultipleSellection(boolean b){
    sc.setMultipleSellection(b);
}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CollectionView dialog = new CollectionView(new javax.swing.JFrame(), true,new CollectionOperator(new File("collection")));
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


 



}

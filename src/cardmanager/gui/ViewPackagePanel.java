/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ViewPackagePanel.java
 *
 * Created on 22.10.2010, 22:54:34
 */

package cardmanager.gui;

import cardmanager.FileBearer;
import cardmanager.impl.packages.PackageDeffinition;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 *
 * @author Jirka
 */
public class ViewPackagePanel extends javax.swing.JPanel {
    private SelectCardComponent view;
    private PackageDeffinition packagee;

    public SelectCardComponent getView() {
        return view;
    }


    /** Creates new form ViewPackagePanel */
    public ViewPackagePanel(PackageDeffinition packagee) {
        initComponents();
        this.packagee=packagee;
        view=new SelectCardComponent(new ArrayList<FileBearer>(0));
        view.setViewModality(true);
        view.setMultipleSellection(false);
        this.add(view);
      refreshView();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridLayout());
    }// </editor-fold>//GEN-END:initComponents

    public void refreshView() {
        Object l=null;
        try{
         l=view.getList().getSelectedValue();
        }catch (Exception ex){

        }
         view.getList().setModel(new PackageDeffBasedModel(packagee));
         view.getList().setSelectedValue(l, true);

    }
  private class PackageDeffBasedModel extends AbstractListModel{
        private PackageDeffinition p;

    public PackageDeffBasedModel(PackageDeffinition p) {
        this.p=p;
    }

        public int getSize() {
            return p.getCards().size();
        }

        public Object getElementAt(int index) {
            return p.getCards().get(index);
        }

}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}

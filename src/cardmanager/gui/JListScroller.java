/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.gui;

import cardmanager.impl.Settings;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author Jirka
 */
public class JListScroller extends Thread{
JScrollPane scrolled;
Settings settings;
    private JList list;

    


    
   public  JListScroller(JScrollPane jScrollPane1, Settings settings, JList jList1) {
        this.scrolled = jScrollPane1;
        this.settings = settings;
        this.list=jList1;
          this.start();
    }






    @Override
    public void run() {
        while(true){
            try{
                Thread.sleep(500);
                if (settings.getAutoScroll()){
//                    if (list.getModel()==null) continue;
//                    if (list.getModel().getSize()<=1) continue;
//                   list.setSelectedValue(list.getModel().getElementAt(list.getModel().getSize()-1), true);
                    scrolled.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
                }
            }catch(Throwable t){
                t.printStackTrace();
            }
        }
    }


}

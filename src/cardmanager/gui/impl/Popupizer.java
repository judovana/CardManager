/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.gui.impl;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;

/**
 *
 * @author Jirka
 */
public class Popupizer {

    int id;

    public Popupizer() {
        id=1;

    }
    public Popupizer(int i) {
        id=i;

    }

    public int getKeyEvent(){
        return getKeyEvent(id);
    }
    public int getKeyEvent(int i){
        switch (i){
            case 0: return KeyEvent.VK_0;
            case 1: return KeyEvent.VK_1;
            case 2: return KeyEvent.VK_2;
            case 3: return KeyEvent.VK_3;
            case 4: return KeyEvent.VK_4;
            case 5: return KeyEvent.VK_5;
            case 6: return KeyEvent.VK_6;
            case 7: return KeyEvent.VK_7;
            case 8: return KeyEvent.VK_8;
            case 9: return KeyEvent.VK_9;

            default: throw new IllegalArgumentException("Int i must be 0-9 includedt. Isnt: "+i);
        }

    }
    public void addAndInc(JMenuItem j){
     j.setMnemonic(getKeyEvent());
j.setText(id+" "+j.getText());
//Setting the accelerator:
     if (!(j instanceof  JMenu))   {
j.setAccelerator(KeyStroke.getKeyStroke(
        getKeyEvent(), ActionEvent.ALT_MASK));
     }
        id++;
        if (id>9)id=0;
    }




}

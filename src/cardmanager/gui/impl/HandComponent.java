/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.gui.impl;


import cardmanager.impl.Settings;
import cardmanager.impl.card.Card;
import cardmanager.impl.packages.Package;
import java.awt.GridLayout;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

/**
 *
 * @author Jirka
 */
public class HandComponent extends JPanel implements Observer {
cardmanager.impl.packages.Package backend;
    private Settings settings;

    public HandComponent(Settings s) {
        this.backend = s.getTemp().getPackage();
    settings=s;
        if (backend instanceof  Observable){
            ((Observable)backend).addObserver(this);
        }
        setLayout(new GridLayout(1, backend.getHand().size()));
        update(null, null);
    }

    public Package getBackend() {
        return backend;
    }



    public void update(Observable o, Object arg) {
        removeAll();
        List<Card> l = backend.getHand();
        setLayout(new GridLayout(1, l.size()));
        for (int i = 0; i < l.size(); i++) {
            Card card = l.get(i);
            this.add(new SimpleCardComponent(settings, card));

        }
        validate();
        repaint();
        

    }



}

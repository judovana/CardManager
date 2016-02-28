/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.gui.impl;

import cardmanager.gui.ImageComponetn;
import cardmanager.impl.Settings;
import cardmanager.impl.card.Card;
import cardmanager.impl.card.CardCache;
import cardmanager.networking.impl.AbstractNetworkGame;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Jirka
 */
public class SimpleCardComponent extends ImageComponetn {

    Settings settings;
    Card card;
    private static Settings SETTINGS;
    private static SimpleCardComponent draged;
    private final SimpleCardComponent self;

    private SimpleCardComponent(BufferedImage i) {
        super(i);
        settings = SETTINGS;
        self = this;
    }

    public Card getCard() {
        return card;
    }



    public SimpleCardComponent(final Settings settings, Card card) {
        super(CardCache.getInstance().getImage(card.getDef().getId()));
        this.settings = settings;
        SETTINGS = settings;
        this.card = card;
        self = this;



        this.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
              

            }

            public void mousePressed(MouseEvent e) {
                draged = self;



            }

            public void mouseReleased(MouseEvent e) {
                draged = null;
            }

            public void mouseEntered(MouseEvent e) {
                if (draged != null) {
                    Container panel = draged.getParent();
                    Component c = panel.getComponentAt(e.getX() + e.getComponent().getX(), e.getY() + e.getComponent().getY());
                    for (int i = 0; i < panel.getComponentCount(); i++) {
                        if (panel.getComponent(i) == c) {
                            panel.add(draged, i);
                            panel.validate();
                            Component[] cc=panel.getComponents();
                            Card[] ccc=new Card[cc.length];
                            for (int j = 0; j < cc.length; j++) {
                                ccc[j]=((SimpleCardComponent)cc[j]).card;

                            }

                            ((HandComponent)panel).getBackend().refactorHand(ccc);
                            break;
                        }
                    }

                }
            }

            public void mouseExited(MouseEvent e) {
            }
        });

    //menu
    //add to tabele
    //add to table flipped
    //move to mypiles...submenu
        JMenuItem jmi2=new JMenuItem("add to table");
        jmi2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                settings.getTemp().getGameViewOutput().add(self.card, true);
                //remove from hand
                settings.getTemp().getPackage().removeFromHand(self.card);
                //notify updaters!
            }
        });
        JMenuItem jmi3=new JMenuItem("add to table fliped");
         jmi3.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                settings.getTemp().getGameViewOutput().add(self.card, false);
                //remove from hand
                settings.getTemp().getPackage().removeFromHand(self.card);
                //notify updaters!
            }
        });
       JMenu jmi4=new JMenu("move to my piles");

       JMenuItem jmi1=new JMenuItem(getCard().getDef().getId());
       jmi1.setEnabled(false);
       getMenu().add(jmi1);
       getMenu().add(jmi2);
       getMenu().add(jmi3);
       getMenu().add(jmi4);
       popupizer.addAndInc(jmi1);
       popupizer.addAndInc(jmi2);
       popupizer.addAndInc(jmi3);
       popupizer.addAndInc(jmi4);
       List<String> l=settings.getTemp().getPackage().getPilesNamesWithCount();
        for (int i = 0; i < l.size(); i++) {
            final String string = l.get(i);
            JMenuItem jmi=new JMenuItem(string);
            jmi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    String id=AbstractNetworkGame.getNameFromNameAndCount(string);
             settings.getTemp().getPackage().getPile(id).add(self.card);
             settings.getTemp().getPackage().removeFromHand(self.card);
             settings.getTemp().getNetwork().messageToServerForAll(" have  removed card from hand ("+settings.getTemp().getPackage().getHand().size()+") to "+id+" ("+settings.getTemp().getPackage().getPile(id).size()+")");
//             Container c=self.getParent();
//             c.remove(self);
//             c.validate();
                }
            });
            jmi4.add(jmi);
            popupizer.addAndInc(jmi);

        }


    }

    @Override
    public int getImageWidth() {
        try {
            return settings.getCardWidth().intValue();
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public int getImageHeight() {
        try {
            return settings.getCardHeight().intValue();
        } catch (Exception ex) {
            return 0;
        }
    }
}

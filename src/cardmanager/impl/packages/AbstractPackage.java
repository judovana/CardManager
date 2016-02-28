/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.packages;

import cardmanager.impl.Settings;
import cardmanager.impl.card.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author Jirka
 */
public abstract  class AbstractPackage extends Observable implements Package{
public static final String STD_IN="Standart input";

protected List<Card> hand=new ArrayList();;
 




   

  








    public static Package unpackPackageDef(Settings s, PackageDeffinition def){
        PackageDef p=new PackageDef();
        p.piles=new HashMap<String, List<Card>>(def.getPiles().size()+1);
        List<Card> stdIn=new ArrayList(def.getCardCount());
        
         for (CountedCard cd : def.getCards()) {

            for(int i=0;i<cd.getCount().intValue();i++){
             stdIn.add(new Card(cd.getCard(),s.getName()));
            }

        }
        Collections.shuffle(stdIn);
        Collections.shuffle(stdIn);

        p.piles.put(STD_IN,stdIn);
        for(int i=0;i<def.getPiles().size();i++){
            p.piles.put(def.getPiles().get(i),new ArrayList(def.getCardCount()));
        }


        return p;

    }

  



    public void addToHand(Card c) {
        hand.add(c);
        super.setChanged();
        notifyObservers("hand++");
    }

    public List<Card> getHand() {
       return Collections.unmodifiableList(hand);
    }

 



    public Card removeFromHand(Card card) {
       if (hand.remove(card)) {
            super.setChanged();
        notifyObservers("hand++");
           return card;
       } else return null;
    }

    public void refactorHand(Card[] c) {
        for (int i = 0; i < c.length; i++) {
            Card card = c[i];
            hand.set(i, card);

        }
    }


}

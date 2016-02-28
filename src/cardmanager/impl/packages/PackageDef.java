/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.packages;

import cardmanager.impl.card.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Jirka
 */
public class PackageDef extends AbstractPackage{

 
protected  Map<String,List<Card>> piles;

public Card getTopFrom(String id){
        List<Card> l = getPile(id);
        Card c=null;
        if (l.size()>0){
        c=l.remove(0);
        }
    return c;
}

   

    public Map<String, List<Card>> getPiles() {
        return piles;
    }

    public List<Card> getPile(String id){
        return piles.get(id);

    }

    public List<String> getPilesNames(){
        List<String> r=new ArrayList(piles.size());
        Set<Entry<String, List<Card>>> entries = piles.entrySet();
        for (Entry<String, List<Card>> entry : entries) {
            r.add(entry.getKey());
        }
        return r;
    }





    public List<String> getPilesNamesWithCount() {
        List<String> r=new ArrayList(piles.size());
        Set<Entry<String, List<Card>>> entries = piles.entrySet();
        for (Entry<String, List<Card>> entry : entries) {
            r.add(entry.getKey()+" ("+entry.getValue().size()+")");
        }
        return r;
    }



 

    public void sufflePile(String pile) {
        List<Card> q = getPile(pile);
        if (q!=null) {
            Collections.shuffle(q);

        }
        
    }



    public Card selectCard(Card c, String pile) {
            List<Card> q = getPile(pile);
        if (q==null) return null;
            if (q.remove(c)) return c;else return null;
            

    }

    @Override
    public Card removeFromHand(Card card) {
       if (hand.remove(card)) {
            super.setChanged();
        notifyObservers("hand++");
           return card;
       } else return null;
    }

    @Override
    public void refactorHand(Card[] c) {
        for (int i = 0; i < c.length; i++) {
            Card card = c[i];
            hand.set(i, card);

        }
    }


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.packages;

import cardmanager.impl.card.*;
import java.awt.Component;
import java.util.*;

/**
 *
 * @author Jirka
 */
public interface Package {

    public Card getTopFrom(String id);

    public List<Card> getHand();
    public void  addToHand(Card c);

    public Map<String, List<Card>> getPiles();

    public List<Card> getPile(String id);

    public List<String> getPilesNames();
    public List<String> getPilesNamesWithCount();

    public void refactorHand(Card[] c);

    public Card removeFromHand(Card card);

   

    public Card selectCard(Card q, String pile);

    public void sufflePile(String pile);

}

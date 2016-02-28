/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.gui.impl.drags;

import cardmanager.impl.card.Card;
import cardmanager.impl.card.CardGeometry;

/**
 *
 * @author Jirka
 */
public class CardDrag extends Drag{

public Card affected;

@Override
    public CardGeometry getAffectedsGeomethry() {
        return affected.getCardGeometry();
    }

}

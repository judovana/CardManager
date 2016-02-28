/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.gui.impl.drags;

import cardmanager.impl.card.Card;
import cardmanager.impl.card.CardGeometry;
import cardmanager.impl.card.Marker;

/**
 *
 * @author Jirka
 */
public class MarkerDrag extends Drag{

public Marker affected;

    @Override
    public CardGeometry getAffectedsGeomethry() {
        return affected.getGeometry();
    }

}

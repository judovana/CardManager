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
public abstract class Drag {
public CardGeometry old;
public double x;
public double y;
public abstract  CardGeometry getAffectedsGeomethry();


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.gui;

import cardmanager.impl.packages.CardDefinition;
import cardmanager.impl.*;
import cardmanager.FileBearer;
import java.io.File;

/**
 *
 * @author Jirka
 */
public class CardInstanceViewForSelectComponent implements FileBearer{

     private CardDefinition card;

    public CardInstanceViewForSelectComponent(CardDefinition get) {
        this.card=get;
    }

    @Override
    public String toString() {
       return (spaceing(card.getName())+"("+card.getId()+")");
    }

    public CardDefinition getCard() {
        return card;
    }

    

    public File getFile() {
        return card.getFile();
    }

        private String spaceing(String name) {
            while(name.length()<20){
                name=name+" ";
            }
            return name;
        }

   

}

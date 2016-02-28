/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.packages;

import cardmanager.impl.*;
import cardmanager.FileBearer;
import java.io.File;

/**
 *
 * @author Jirka
 */
public class CountedCard implements FileBearer{
    CardDefinition card;
    Integer count=1;

    @Override
    public String toString() {
        return card.getName()+" ("+count.toString()+")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  CardDefinition) return card.equals(obj);
        if (obj instanceof  CountedCard) return card.equals(((CountedCard)obj).getCard());
        return false;

    }

    @Override
    public int hashCode() {
      return card.hashCode();
    }


    public CountedCard(CardDefinition card) {
        this.card = card;
    }

    public CountedCard(CardDefinition card,Integer count) {
        this.card = card;
        setCount(count);
    }

    public void setCount(Integer count){
        if (count.intValue()<=0) {
            throw new CardsUnderOneException("Count cant be less then 1!");
        }
        this.count = count;

    }

    public Integer getCount() {
        return count;
    }




    public CardDefinition getCard() {
        return card;
    }

    public File getFile() {
        return card.getFile();
    }



private class CardsUnderOneException extends RuntimeException{

        public CardsUnderOneException(String string) {
            super(string);
        }

}
}

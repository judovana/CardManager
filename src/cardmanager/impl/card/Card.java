/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.impl.card;

import cardmanager.FileBearer;
import cardmanager.impl.CollectionOperator;
import cardmanager.impl.packages.CardDefinition;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Jirka
 */
public class Card implements FileBearer {

    private CardDefinition def;
    private String owener;
    private CardGeometry cardGeometry;
    private long id;
    private static AtomicLong ID = new AtomicLong(System.currentTimeMillis());
    private boolean face = true;

    public long getId() {
        return id;
    }

    public Card(CardDefinition cd, String name) {
        def = cd;
        owener = name;

        id = ID.addAndGet(1);


    }

    public static List<Card> cardsFromSentences(String q, CollectionOperator cl) {
        String[] c = q.split(";");
        ArrayList<Card> r = new ArrayList<Card>(c.length);
        for (int i = 0; i < c.length; i++) {
            String string = c[i];
            r.add(cardFromSentence(string, cl));


        }

        return r;

    }

    public static Card cardFromSentence(String c, CollectionOperator cl) {
        String[] elements = c.split(":");
        long id = new Long(elements[0]);
        String cardDef = (elements[1]);
        int x = new Integer(elements[2]);
        int y = new Integer(elements[3]);
        double r = new Double(elements[4]);
        boolean face = new Boolean(elements[5]);
        Card crd = new Card(cl.getById(cardDef), "temp");
        crd.setCardGeometry(new CardGeometry(x, y, r));
        crd.setId(id);
        crd.setFace(face);
        return crd;
    }

    public String getSentence() {
        Card card = this;

        if (getCardGeometry() == null) {
            setCardGeometry(new CardGeometry(0, 0, 0));

        }
        StringBuilder sb = new StringBuilder();

        sb.append(card.getId()).append(":").append(card.getDef().getId()).append(":").
                append(card.getCardGeometry().getSentence()).append(":").
                append(card.isFace()).append(";");

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CardDefinition) {
            return ((CardDefinition) obj).equals(this.def);
        }

        if (obj instanceof Card) {
            return ((Card) obj).id == this.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    /**
     * @return the def
     */
    public CardDefinition getDef() {
        return def;
    }

    /**
     * @param def the def to set
     */
    public void setDef(CardDefinition def) {
        this.def = def;
    }

    /**
     * @return the owener
     */
    public String getOwener() {
        return owener;
    }

    public void setFace(boolean face) {
        this.face = face;
    }

    public boolean isFace() {
        return face;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @param owener the owener to set
     */
    public void setOwener(String owener) {
        this.owener = owener;
    }

    /**
     * @return the cardGeometry
     */
    public CardGeometry getCardGeometry() {
        return cardGeometry;
    }

    /**
     * @param cardGeometry the cardGeometry to set
     */
    public void setCardGeometry(CardGeometry cardGeometry) {
        this.cardGeometry = cardGeometry;
    }

    public File getFile() {
        return def.getFile();
    }

    @Override
    public String toString() {
        return (spaceing(def.getName()) + "(" + def.getId() + ")");
    }

    private String spaceing(String name) {
        while (name.length() < 20) {
            name = name + " ";
        }
        return name;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.impl.card;

import cardmanager.impl.card.CardGeometry;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Marker {

    private static final Random idHelepr = new Random();
    CardGeometry geometry;
    Color color;
    String id;
    String txt;

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }



    public CardGeometry getGeometry() {
        return geometry;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public void setGeometry(CardGeometry geometry) {
        this.geometry = geometry;
    }

    private Marker(CardGeometry geometry, Color color, String id,String txt) {
        this.geometry = geometry;
        this.color = color;
        this.id = id;
        this.txt=txt;
    }

    public Marker(Color color,String txt) {
        this(0, 0, color,txt);
    }

    public Marker(int x, int y, Color color,String txt) {
        this(new CardGeometry(x, y, 0), color, generateID(color,txt),txt);
    }

    public static String generateID(Color c,String txt) {
        int i = idHelepr.nextInt();
        Date dt = new Date();
        String clr = Integer.toHexString(c.getRGB()).substring(2).toUpperCase();
        Integer hash =txt.hashCode();
        return i + "#" + clr + "#" + dt.getTime()+"#"+hash.toString();


    }

    public static Marker markerFromSentence(String c) {
        String[] elements = c.split(":");
        String id = elements[0];
        Color color = new Color(new Integer(elements[1]));
        String TXT = elements[2];
        String txt=TXT.replace("URUGUL",":").replace("IOUNMB",";");
        int x = new Integer(elements[3]);
        int y = new Integer(elements[4]);
        double r = new Double(elements[5]);
        Marker m = new Marker(new CardGeometry(x, y, r), color, id,txt);
        return m;
    }

    public static List<Marker> markerssFromSentences(String q) {
        String[] c = q.split(";");
        List<Marker> r = new ArrayList<Marker>(c.length);
        for (int i = 0; i < c.length; i++) {
            String string = c[i];
            r.add(markerFromSentence(string));
        }

        return r;

    }

    public String getSentence() {


        if (getGeometry() == null) {
            setGeometry(new CardGeometry(0, 0, 0));

        }
        StringBuilder sb = new StringBuilder();
        String TXT=txt.replace(":", "URUGUL").replace(";", "IOUNMB");
        sb.append(getId()).append(":").append(color.getRGB()).append(":").append(TXT).append(":").
                append(getGeometry().getSentence()).append(";");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Marker)) {
            return false;
        }
        return this.getId().equals(((Marker) obj).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

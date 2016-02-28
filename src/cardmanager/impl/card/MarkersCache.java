/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.impl.card;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jirka
 */
public class MarkersCache {

    private static MarkersCache instance;

    public static MarkersCache getInstance(int radius) {
        if (instance == null) {
            instance = new MarkersCache(radius);
        }
        return instance;
    }
    private Map<String, BufferedImage> cache;
    private final int radius;

    private MarkersCache(int radius) {
        this.radius = radius;
        cache = new HashMap(10);
    }

    public BufferedImage getMarkerImage(Color c, String txt) {
        String gid = c.getRGB() + "#" + txt;
        BufferedImage bi = cache.get(gid);
        Graphics2D g;
        if (bi == null) {
            try {
                bi = new BufferedImage(radius, radius, BufferedImage.TYPE_INT_ARGB);
                g = bi.createGraphics();
                g.setFont(g.getFont().deriveFont((3f*g.getFont().getSize2D())/4f));
                int sw = g.getFontMetrics().stringWidth(txt);
                int sh = g.getFontMetrics().getHeight();
                g.setColor(c);
                g.fillOval(0, 0, radius, radius);
                g.setColor(invert(c));
                g.drawString(txt, radius / 2 - sw / 2, radius / 2 + sh / 2);
            } catch (Exception ex) {
                ex.printStackTrace();
                bi = null;
            }

            cache.put(gid, bi);
            return bi;
        } else {
            return bi;
        }
    }

    public static Color invert(Color bgColor) {
        return new Color(255 - bgColor.getRed(),
                255 - bgColor.getGreen(),
                255 - bgColor.getBlue());
    }
}

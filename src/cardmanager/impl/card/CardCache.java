/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.impl.card;

import cardmanager.impl.CollectionOperator;
import cardmanager.impl.Settings;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 *
 * @author Jirka
 */
public class CardCache {

    private static CardCache instance;

    public static CardCache getInstance() {
        return instance;
    }

    static {
        try {
            instance = new CardCache(CollectionOperator.DEF_DIR, Settings.DEF_BGS);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private Map<String, BufferedImage> cache;
    private Set<String> blacklist=new HashSet<String>();
    private File dir;
    private File bgDir;
    private BufferedImage noimage;

    private CardCache(File DEF_DIR, File bgDir) throws IOException {
        dir = DEF_DIR;
        this.bgDir = bgDir;
        if (!dir.exists()) {
            throw new FileNotFoundException(dir + " doesnot exists");
        }
        if (!dir.isDirectory()) {
            throw new FileNotFoundException(dir + " is not direcory");
        }
        if (!bgDir.exists()) {
            throw new FileNotFoundException(dir + " doesnot exists");
        }
        if (!bgDir.isDirectory()) {
            throw new FileNotFoundException(dir + " is not direcory");
        }
        noimage = ImageIO.read(new File("data/noimage.jpg"));
        cache = new HashMap(100);
    }

    private BufferedImage getImageFromCache(String background, File dir) {
         BufferedImage bi = cache.get(background);
        if (bi == null && !blacklist.contains(background)) {
            try {
                bi = ImageIO.read(new File(dir, background));
            } catch (Exception ex) {
                blacklist.add(background);
                ex.printStackTrace();
                bi = null;
            }
            if (bi == null) {
                return noimage;
            }

            cache.put(background, bi);
            return bi;
        } else {
             if (bi == null) {
                return noimage;
            }
            return bi;
        }
    }
    public BufferedImage getBackground(String background) {
      return getImageFromCache(background, bgDir);
    }

    public BufferedImage getImage(String id) {
      return getImageFromCache(id, dir);
    }
}

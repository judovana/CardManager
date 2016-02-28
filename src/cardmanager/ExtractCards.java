/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Jirka
 */
public class ExtractCards {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File todo=new File("./collection/todo/3x3");
        File output=new File("C:\\todoed/");
        output.mkdir();
        File[] jpgs=todo.listFiles();
        int counter=0;
        for (int i = 0; i < jpgs.length; i++) {
            File file = jpgs[i];
            System.out.println("read "+ file.toString() );
            BufferedImage im=ImageIO.read(file);
            int w=im.getWidth();
            int h=im.getHeight()-120;
            int ws=w/3;
            int hs=h/3;
            for (int x=0;x<3;x++)
                for (int y=0;y<3;y++){
                    counter++;
                    BufferedImage cu=new BufferedImage(ws, hs, im.getType());
                    cu.createGraphics().drawImage(im.getSubimage(x*ws,y*hs,ws,hs),0,0,null);
                    File f=new File(output,nuluj(counter)+".jpg");
                    System.out.println("write"+ f.toString() );
                    ImageIO.write(cu, "jpg", f);

                }




        }


    }

    private static String nuluj(int counter) {
        String s=String.valueOf(counter);
        while (s.length()<5){
            s="0"+s;
                    }
        return "BaseSet-"+s;

    }

}

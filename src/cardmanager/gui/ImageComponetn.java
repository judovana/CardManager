/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.gui;

import cardmanager.gui.impl.Popupizer;
import cardmanager.gui.impl.SimpleCardComponent;
import java.awt.event.MouseAdapter;




import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 *
 * @author jvanek
 */
public class ImageComponetn extends JComponent{

    protected BufferedImage image;
    protected double zoom=1;
    private JPopupMenu menu;
    protected final ImageComponetn self;
    private static File lastDir;
    protected Popupizer popupizer=new Popupizer();
   
 

    private boolean viewModality=false;

    public void setViewModality(boolean viewModality) {
        this.viewModality = viewModality;
    }

    public boolean getViewModality(){
        return viewModality;
    }
    public ImageComponetn(BufferedImage image) {
        this();
        this.image = image;
        setZoom();
        createMenu();

    }

    public ImageComponetn(File image) throws IOException {
        this((ImageIO.read(image)));


    }
    public ImageComponetn(String image) throws IOException {
        this(new File(image));


    }


    @Override
    public void paint(Graphics g) {
        if (image!=null){
        setZoom();
        int w=(int)((double)getImageWidth()*zoom);
        int h=(int)((double)getImageHeight()*zoom);
            if (image!=null) g.drawImage(image,getWidth()/2-w/2,getHeight()/2-h/2, w,h, this);
        }
    }
public Dimension getResizedImageSize(){
    setZoom();
        int w=(int)((double)getImageWidth()*zoom);
        int h=(int)((double)getImageHeight()*zoom);
        return new  Dimension(w, h);
}

    public double getZoom() {
        return zoom;
    }


   public int  getImageLeft(){
         setZoom();
        int w=(int)((double)getImageWidth()*zoom);
        int h=(int)((double)getImageHeight()*zoom);
            if (image!=null) return getWidth()/2-w/2;
        return -1;
    }

     public int  getImageTop(){
         setZoom();
        int w=(int)((double)getImageWidth()*zoom);
        int h=(int)((double)getImageHeight()*zoom);
            if (image!=null) return getHeight()/2-h/2;
        return -1;
    }

    @Override
    public void paintComponent(Graphics g){
        paint(g);
    }

    public JPopupMenu getMenu() {
        return menu;
    }

    


    public ImageComponetn() {
        self=this;
    }

public int getImageWidth(){
    if (image==null)return 0;
    return image.getWidth();
}
public int getImageHeight(){
    if (image==null)return 0;
    return image.getHeight();
}

       
    private void setZoom() {
        if (image==null){
            zoom=1;
        }else{
            

                double zoom1=(double)getWidth()/(double)getImageWidth();
            
                double zoom2=(double)getHeight()/(double)getImageHeight();

                zoom=Math.min(zoom1, zoom2);
                
            
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        setZoom();
    }


public BufferedImage expand() {
    return expand(viewModality,null,null);
}

public BufferedImage expand(BufferedImage i) {
    return expand(i,false,null,null);
}
public BufferedImage  expand(boolean modal,String title,ImageComponetn clazz) {
    return expand(self.image, modal, title, clazz);
}

public static BufferedImage expand(BufferedImage i,boolean modal,String title,ImageComponetn clazz) {
                    JDialog d = new JDialog((JFrame) null, "image", modal);
                    if (title!=null){
                        d.setTitle(title);
                    }
                    d.setLayout(new GridLayout(1, 1));
                    ImageComponetn k;
                    if (clazz==null){
//                        if (this instanceof  SimpleCardComponent){
//                         k= new SimpleCardComponent(i);
//                        }else
                        {
                        k = new ImageComponetn(i);
                        }
                    }
                    else k=clazz;
                    k.createMenu();
                    d.add(k);
                    d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    d.setSize(800, 600);
                    d.setLocationRelativeTo(null);
                    d.setVisible(true);
                    return k.getImage();
                }



public void createMenu(){
        if (menu==null){
        this.menu=new JPopupMenu();

        JMenuItem jmi2=new JMenuItem("view");
        JMenuItem jmi3=new JMenuItem("save as");


;

               jmi2.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    image=expand();
                    repaint();

                }


            });
            menu.add(jmi2);
            popupizer.addAndInc(jmi2);

               jmi3.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (image==null)return;
                 javax.swing.JFileChooser jfch=new javax.swing.JFileChooser(lastDir);
                 int k=jfch.showSaveDialog(self);
                 if (k==JFileChooser.APPROVE_OPTION){
                     lastDir=jfch.getCurrentDirectory();
                     try{
                     ImageIO.write(image,
                             jfch.getSelectedFile().getName().substring(jfch.getSelectedFile().getName().lastIndexOf(".")+1), jfch.getSelectedFile());
                     }catch(Exception ex){
                         ex.printStackTrace();
                         JOptionPane.showMessageDialog(self, ex);
                     }
                 }



                }
            });
            menu.add(jmi3);
            popupizer.addAndInc(jmi3);


self.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getButton()==MouseEvent.BUTTON3){
                        menu.show(self, e.getX(), e.getY());
                    }
                }

});

        }
    }



}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packagegrabber;

import java.io.IOException;

/**
 *
 * @author jvanek
 */
public class Main {

    public static final String DEFAULT_PREFIX = "/MagicTheGathering-all/";
    private static boolean save=false;
    private static boolean cards=false;
    private static String  prefix=DEFAULT_PREFIX;
    private static int anispam=500;

    private static final String pc="--prefix";
    private static final String sc="--save";
    private static final String ac="--antispam";
    private static final String cc="--cards";


    
     
static String[]test={"http://magic.tcgplayer.com/db/deck.asp?deck_id=52553",
      "http://magic.tcgplayer.com/db/deck.asp?deck_id=52540",
      "http://magic.tcgplayer.com/db/deck.asp?deck_id=51835",
      "http://magic.tcgplayer.com/db/deck.asp?deck_id=51818",
      "http://magic.tcgplayer.com/db/deck.asp?deck_id=51476",
      "http://magic.tcgplayer.com/db/deck.asp?deck_id=51273"};
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String[] test={
//            Main.test[0],
//            cc+"=true"
//        };
//        args=test;

        if (args.length==0){
            System.out.println("["+pc+"=? | "+sc+"=? | "+ac+"=?] url1 url2 url3 ... urlN");
            System.out.println("Application will download selected urls as tcgplayer packages");
            System.out.println(pc+"=some_prefix - default is "+DEFAULT_PREFIX+", it is path to card in your collection");
            System.out.println(sc+"=true|false - default is false, if true, urls will be downlaoded to files of their name got from pacakge page");
            System.out.println(ac+"=NUMBER - default is "+anispam+", miliseconds of delay between accesing urls (if you are to fast, you can get ban!)");
            System.out.println(cc+"=true|false - default is false, if true, also images of cards will be downlaoded");
            return;
        }
        for (int i = 0; i < args.length; i++) {
            String string = args[i];
            boolean processed=true;
            try{
                if (string.startsWith(pc+"=")){
                    prefix=string.split("=")[1];
                }else if (string.startsWith(sc+"=")){
                    save=Boolean.valueOf(string.split("=")[1]);
                }else if (string.startsWith(ac+"=")){
                    anispam=Integer.valueOf(string.split("=")[1]);
                }else if (string.startsWith(cc+"=")){
                    cards=Boolean.valueOf(string.split("=")[1]);
                }else{
                    processed=false;
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                if (processed){
                args[i]="";
                }
            }
            
        }
       PackageGrabber.anti_spam_sleep=anispam;
       CardGetter.anti_spam_sleep=anispam;
        for (int i = 0; i < args.length; i++) {
            String string = args[i];
            if (string==null || string.trim().equals("")){
                continue;
            }
        try{
        PackageGrabber pg = new PackageGrabber(string);
        pg.setPrefix(prefix);
        pg.setCards(cards);
        pg.download();
            System.out.println(pg.getName());
            pg.printResult(System.out);
            if (save){
                pg.save();
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
        }
    }



}

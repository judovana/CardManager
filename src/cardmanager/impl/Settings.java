/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.impl;

import java.io.File;
import java.io.StringReader;

/**
 *
 * @author Jirka
 */
public class Settings extends SettingsFather{

        private static final String CARD_WIDTH="cardWidth";
        private static final String CARD_HEIGHT="cardHeight";
        private static final String BACKGROUND="background";
        private static final String NAME="name";
        private static final String LAST_IP="lastIP";
        private static final String LAST_PORT="lastPort";
        private static final String SHARED_PILE="sharedPile";
        private static final String AUTO_SCROLL="autoScroll";
        private static final String MARKER_RADIUS="markerRadius";


   
    public static final String DEF_PATH = "data/globalSettings.txt";
    public static final String DEFAULT = "" +
            "cardWidth=120\n" +
            "cardHeight=170\n" +
            "background=uglyDefoult.jpg\n" +
            "name=Jirka\n" +
            "lastIP=0.0.0.0\n" +
            "lastPort=41583\n"+
            "sharedPile=false\n" +
            "markerRadius=50\n" +
            "autoScroll=true\n";

    public static File DEF_BGS=new File("data/backgrounds");
  
    private TempSettings tempSettings=new TempSettings();


    public Settings(File f) {
      super(f);
    }

    public Settings(int a) {

        backendFile = new File(DEF_PATH);
        if (!backendFile.exists()) {
            try {
                load(new StringReader(DEFAULT));
                save();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try {
            load(backendFile);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public boolean getImServer() {
        return getBoolean("imServer");
    }

    public TempSettings getTemp() {
        return tempSettings;
    }

    public boolean setImServer(boolean selected) {
        return setBoolean("imServer", selected);

    }





    
   
   

    public static void main(String args[]) {
        new Settings(0);
    }

  

   
    public String getName(){
        return getString(NAME);
    }
    public String setName(String name) {

        return setString(NAME,name);
    }

          

        public Integer getCardWidth(){
            return getInteger(CARD_WIDTH);
        }

        public Integer setCardWidth(Integer value){
            return setInteger(CARD_WIDTH,value);
        }

        public Integer getMarkerRadius(){
            return getInteger(MARKER_RADIUS);
        }

        public Integer setMarkerRadius(Integer value){
            return setInteger(MARKER_RADIUS,value);
        }

        public Integer getCardHeight(){
            return getInteger(CARD_HEIGHT);
        }

        public Integer setCardHeight(Integer value){
            return setInteger(CARD_HEIGHT,value);
        }


           public String getBackground(){
        return getString(BACKGROUND);
    }
    public String setBackground(String value) {

        return setString(BACKGROUND,value);
    }
         
             public String getLastIp(){
        return getString(LAST_IP);
    }
    public String setLastIp(String ip) {

        return setString(LAST_IP,ip);
    }
          
            

        public Integer getLastPort(){
            return getInteger(LAST_PORT);
        }

        public Integer setLastPort(Integer value){
            return setInteger(LAST_PORT,value);
        }

        public boolean setSharedPile(Boolean value){
            return setBoolean(SHARED_PILE, value);
        }
        public boolean getSharedPile(){
            return getBoolean(SHARED_PILE);
        }

         public boolean setAutoScroll(Boolean value){
            return setBoolean(AUTO_SCROLL, value);
        }
        public boolean getAutoScroll(){
            return getBoolean(AUTO_SCROLL);
        }

    public void clearTemp() {
        CollectionOperator cl=tempSettings.getCollection();
        tempSettings=new TempSettings();
        tempSettings.setCollection(cl);
    }

}

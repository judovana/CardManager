/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.networking.impl;


import cardmanager.networking.NetworkGame;
import cardmanager.impl.*;
import cardmanager.impl.card.Card;
import cardmanager.impl.card.CardGeometry;
import cardmanager.impl.networking.JoinedPlayer;
import cardmanager.impl.networking.SyncKeeper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jirka
 */
public abstract class AbstractNetworkGame implements NetworkGame {

    public static String getNameFromNameAndCount(String name) {
        return name.substring(0, name.lastIndexOf(" ")).trim();
    }



    public  static  String[] parse(String command) {
        String c= (command.substring(0,command.indexOf(":"))).trim();
        String s= (command.substring(command.indexOf(":")+1)).trim();
        String[] t=new  String[2];
        t[0]=c;
        t[1]=s;
                return t;
    }

    public static String prepareNames(String[] toArray) {
       String r= namesAllExceptMee;
        for (int i = 0; i < toArray.length; i++) {
            r=r+":"+toArray[i];

        }
       return r;
    }
     public static String prepareAllNames(String[] toArray) {
       String r= allPlayersInOrder;
        for (int i = 0; i < toArray.length; i++) {
            r=r+":"+toArray[i];

        }
       return r;
    }

    static String[] prepareNames(String s) {
     return s.split(":");
    }
    static String[] prepareAllNames(String s) {
     return s.split(":");
    }

    static String preparePiles(List<String> pilesNamesWithCount) {
        if (pilesNamesWithCount==null || pilesNamesWithCount.size()==0) return "";
        String s=pilesNamesWithCount.get(0);
        for (int i = 1; i < pilesNamesWithCount.size(); i++) {
            s=s+";"+pilesNamesWithCount.get(i);

        }
        return s;

    }

    static String[] parsePiles(String s){
        return s.split(";");
    }

    static String prepareSharedContent(String s) {
        return sharedContent+":"+s.replaceAll("\n","%{xichrgzngt}");
    }
    static String parseSharedContent(String s) {
        return s.replaceAll("%\\{xichrgzngt\\}","\n");
    }


      public static synchronized SyncKeeper syncRequest(JoinedPlayer fromwho,String reqestMessageToFromWho,SyncKeeper waitFor,int timeoout,String message,NetworkLogger log) {
          waitFor.setWaitingFor(null);
          if (fromwho!=null)fromwho.messageToServer(reqestMessageToFromWho);
        int timeout=0;
        while(waitFor.getWaitingFor()==null){
            try{
            Thread.sleep(50);
            timeout++;
            if (timeout>timeoout){
                log.writeLocalMessage(message);
                return null;
            }
            }catch(InterruptedException ex){
                throw new IllegalStateException(ex);
            }
        }

        return waitFor;
    }


      
}

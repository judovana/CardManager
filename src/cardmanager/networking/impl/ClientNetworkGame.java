/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.networking.impl;

import cardmanager.networking.NetworkGameProceeder;
import cardmanager.networking.NetworkGame;
import cardmanager.impl.*;
import cardmanager.impl.card.Card;
import cardmanager.impl.networking.JoinedPlayer;
import cardmanager.impl.networking.SyncKeeper;import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Jirka
 */
public class ClientNetworkGame extends  Observable implements NetworkGame, Observer {
    private Settings settings;
    JoinedPlayer joinedPlayer;
    NetworkGameProceeder proceeder;
     final SyncKeeper<Card[]> waitingForPlayerCardsKeeper=new SyncKeeper<Card[]>() {

        Card[] waitingForPlayerCards=null;

        public Card[] getWaitingFor() {
            return waitingForPlayerCards;
        }

        public void setWaitingFor(Card[] object) {
           waitingForPlayerCards=object;
        }
    };
    
   final SyncKeeper<String[]> waitingForPlayersNamesKeeper=new SyncKeeper<String[]>() {

        String[] waitingForPlayersNames=null;

        public String[] getWaitingFor() {
            return waitingForPlayersNames;
        }

        public void setWaitingFor(String[] object) {
           waitingForPlayersNames=object;
        }
    };
     final SyncKeeper<String[]> waitingForPlayerPilesKeeper=new SyncKeeper<String[]>() {

        String[] waitingForPlayerPiles=null;

        public String[] getWaitingFor() {
            return waitingForPlayerPiles;
        }

        public void setWaitingFor(String[] object) {
           waitingForPlayerPiles=object;
        }
    };
final SyncKeeper<String> waitingForServerNamesKeeper=new SyncKeeper<String>() {

        String waitingForPlayersNames=null;

        public String getWaitingFor() {
            return waitingForPlayersNames;
        }

        public void setWaitingFor(String object) {
           waitingForPlayersNames=object;
        }
    };
    public ClientNetworkGame(Settings settings, JoinedPlayer joinedPalyer) {
        this.settings=settings;
         proceeder=new ClientNetworkGameProceeder(settings,this);
        settings.getTemp().setNetwork(this);
        this.joinedPlayer=joinedPalyer;
        joinedPalyer.startDog();
        joinedPlayer.addObserver(this);
    }

    public void messageToServerForAll(String string) {
        joinedPlayer.messageToServer(plainTextForAll+": "+settings.getName()+": "+string);

    }

 

    public  void update(Observable o, Object arg) {
        String command=(String) arg;
        String[] cc=AbstractNetworkGame.parse(command);
        String c=cc[0];
        String s=cc[1];
        if (c.equals(plainTextForAll)){
            proceeder.proceedPlainText(null,s);
        }else if (c.equals(renameBack)){
            proceeder.proceedRenameBack(s);
        }else if (c.equals(renamed)){
            proceeder.proceedRename(s,null);
        }else if (c.equals(sharedContent)){
            proceeder.proceedSharedContent(s,null);
        }else if (c.equals(namesAllExceptMee)){
            proceeder.proceedNamesAllExceptMee(s,null);
        }else if (c.equals(allPlayersInOrder)){
            proceeder.allPlayersInOrder(s,null);
        }else if (c.equals(needYourPiles)){
            proceeder.proceedNeedYourPiles(null,null);
        }else if (c.equals(myPiles)){
            proceeder.proceedMyPiles(s);
        }else if (c.equals(syncing)){
            proceeder.syncTable(s,null);

         }else if (c.equals(ask)){
            proceeder.ask(s,null);
        }else if (c.equals(serverName)){
            proceeder.serverName(s,null);

         }
    }

    public void renamePlayer(String from, String to) {
        joinedPlayer.messageToServer(renamed+": "+from+": "+to);

    }

    public void sendSharedContent(String s) {
        s=AbstractNetworkGame.prepareSharedContent(s);
       joinedPlayer.messageToServer(s);
    }

    public String[] getPlayersInOrder() {
       
       
        
       SyncKeeper a=syncRequest((allPlayersInOrder+":"+settings.getName()),waitingForPlayersNamesKeeper,"Timeout for getting players names");
        if (a==null) return null;
        return waitingForPlayersNamesKeeper.getWaitingFor();
    }
    
    
    
      public String[] getPlayersExceptMe() {



       SyncKeeper a=syncRequest((namesAllExceptMee+":"+settings.getName()),waitingForPlayersNamesKeeper,"Timeout for getting others players names");
        if (a==null) return null;
        return waitingForPlayersNamesKeeper.getWaitingFor();
    }

        public void replyCards(String nameFrom,String nameTo, String pile,String reqest,String context){
joinedPlayer.messageToServer(ask+":"+nameFrom+"&"+nameTo+"&"+reqest+"&"+pile+"&"+context);
        }

     public Card[] askCards(String nameFrom,String nameTo, String pile,String reqest,String context) {
            
               SyncKeeper l= syncRequest(ask+":"+nameFrom+"&"+nameTo+"&"+reqest+"&"+pile+"&"+context,waitingForPlayerCardsKeeper, "Timeout gettin player's ("+nameFrom+")cards");
               if (l==null) return null;
               return  waitingForPlayerCardsKeeper.getWaitingFor();
          
    }
          
    

    public String[] getPlayerPiles(String playerName) {
        SyncKeeper a=syncRequest(needYourPiles+":"+playerName,waitingForPlayerPilesKeeper,"Timeout for getting others players piles");
        if (a==null) return null;
        return waitingForPlayerPilesKeeper.getWaitingFor();
    }

    private SyncKeeper syncRequest(String r,SyncKeeper waitingFor,String message) {
        return syncRequest(r,waitingFor, 50,message);
    }


     private SyncKeeper syncRequest(String r,SyncKeeper waitingFor, int timeout, String message) {
      return AbstractNetworkGame.syncRequest(joinedPlayer,r,waitingFor, timeout, message, settings.getTemp().getLogger());

    }

    public void syncTable(String createSentence) {
        joinedPlayer.messageToServer(syncing+": "+createSentence);
    }

    public String getServerName() {
        SyncKeeper a=syncRequest(serverName+":",waitingForServerNamesKeeper,"Timeout for getting server names");
       if (a==null) return null;
        return waitingForServerNamesKeeper.getWaitingFor();

    }

    public void shufflePile(String pile) {
        joinedPlayer.messageToServer(shuffle+":"+pile);
    }

    public void closeAll() {
        joinedPlayer.closeAll();

    }


}

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
import cardmanager.impl.networking.SyncKeeper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Jirka
 */
public class ServerNetworkGame extends  Observable implements NetworkGame, Observer{
    private Settings settings;
    ArrayList<JoinedPlayer> joindePlayers;
    NetworkGameProceeder proceeder;

        final SyncKeeper<String[]> waitingForPlayerPilesKeeper=new SyncKeeper<String[]>() {

        String[] waitingForPlayerPiles=null;

        public String[] getWaitingFor() {
            return waitingForPlayerPiles;
        }

        public void setWaitingFor(String[] object) {
           waitingForPlayerPiles=object;
        }
    };

      final SyncKeeper<Card[]> waitingForPlayerCardsKeeper=new SyncKeeper<Card[]>() {

        Card[] waitingForPlayerCards=null;

        public Card[] getWaitingFor() {
            return waitingForPlayerCards;
        }

        public void setWaitingFor(Card[] object) {
           waitingForPlayerCards=object;
        }
    };


    public ServerNetworkGame(Settings settings, ArrayList<JoinedPlayer> joinedPlayers) {
         proceeder=new ServerNetworkGameProceeder(this,settings);
        this.settings=settings;
        this.joindePlayers=joinedPlayers;
        settings.getTemp().setNetwork(this);
        for (Iterator<JoinedPlayer> it = joinedPlayers.iterator(); it.hasNext();) {
            JoinedPlayer joinedPlayer = it.next();
            joinedPlayer.startDog();
            joinedPlayer.addObserver(this);

        }

    }

    public void messageToServerForAllImpl(String string) {
      
        for (JoinedPlayer joinedPlayer : joindePlayers) {
            joinedPlayer.messageToServer(string);
        }
    }

   

    public  void update(Observable o, Object arg) {
         String command=(String) arg;
        String[] cc=AbstractNetworkGame.parse(command);
        String c=cc[0];
        String s=cc[1];
        if (c.equals(plainTextForAll)){
            proceeder.proceedPlainText(command,s);
        }else if (c.equals(renamed)){
           proceeder.proceedRename(s, o);
        } else if (c.equals(AbstractNetworkGame.sharedContent)){
           proceeder.proceedSharedContent(s, o);
        }else if (c.equals(AbstractNetworkGame.namesAllExceptMee)){
           proceeder.proceedNamesAllExceptMee(s, o);
        }else if (c.equals(AbstractNetworkGame.allPlayersInOrder)){
           proceeder.allPlayersInOrder(s, o);
        }else if (c.equals(myPiles)){
           proceeder.proceedMyPiles(s);
        }else if (c.equals(needYourPiles)){
           proceeder.proceedNeedYourPiles(s,(JoinedPlayer)o);
        }else if (c.equals(syncing)){
            proceeder.syncTable(s,o);
        }else if (c.equals(ask)){
            proceeder.ask(s,o);
        }else if (c.equals(serverName)){
            proceeder.serverName(s,o);

         }else if (c.equals(shuffle)){
            proceeder.shufflePile(s);

         }
    }

    public void messageToServerForAll(String string) {
       string=settings.getName()+": "+string;
      this.settings.getTemp().getLogger().writeLocalMessage(string);
       messageToServerForAllImpl(plainTextForAll+":"+string);
    }

    public void renamePlayer(String from, String to) {
          for (int i = 0; i < joindePlayers.size(); i++) {
                JoinedPlayer joinedPlayer = joindePlayers.get(i);
                if (joinedPlayer.getName().equals(to)){
             settings.getTemp().getLogger().writeLocalMessage("You can't rename to to name of some player");
             settings.setName(from);
               String aa=from+" have tried to rename himself to "+to+", but was refused";
             messageToServerForAllImpl(plainTextForAll+":"+aa);
          return;
                }
          }
            String aa=from+" have changed name to"+to;
              messageToServerForAllImpl(renamed+":"+from+":"+to);
          settings.getTemp().getLogger().writeLocalMessage(aa);
    }

   

    public void sendSharedContent(String s) {
        s=AbstractNetworkGame.prepareSharedContent(s);
        for (int i = 0; i < joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = joindePlayers.get(i);
            joinedPlayer.messageToServer(s);

        }

    }

    public String[] getPlayersExceptMe() {
       String[] r=new String[joindePlayers.size()];
        for (int i = 0; i < joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = joindePlayers.get(i);
            r[i]=joinedPlayer.getName();

        }
       return r;
    }

     public String[] getPlayersInOrder() {
       String[] r=new String[joindePlayers.size()+1];
       r[0]=settings.getName();
        for (int i = 0; i < joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = joindePlayers.get(i);
            r[i+1]=joinedPlayer.getName();

        }
       return r;
    }

     public void replyCards(String nameFrom,String nameTo, String pile,String reqest,String context) {
String s=ask+":"+nameFrom+"&"+nameTo+"&"+reqest+"&"+pile+"&"+context;
for (int i = 0; i < joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = joindePlayers.get(i);
            if (joinedPlayer.getName().equals(nameTo)) {
 joinedPlayer.messageToServer(s);
            }
}
}

    public Card[] askCards(String nameFrom,String nameTo, String pile,String reqest,String context) {
        if (context==null) context="";
            for (int i = 0; i < joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = joindePlayers.get(i);
            if (joinedPlayer.getName().equals(nameTo)) {

               SyncKeeper l= syncRequest(joinedPlayer,ask+":"+nameFrom+"&"+nameTo+"&"+reqest+"&"+pile+"&"+context,waitingForPlayerCardsKeeper, "Timeout gettin player's ("+nameTo+")cards");
               if (l==null) return null;
                return  waitingForPlayerCardsKeeper.getWaitingFor();

            }
        
    }
            return null;
    }

    public String[] getPlayerPiles(String playerName) {
        for (int i = 0; i < joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = joindePlayers.get(i);
            if (joinedPlayer.getName().equals(playerName)) {
                
               SyncKeeper l= syncRequest(joinedPlayer,needYourPiles+":"+playerName,waitingForPlayerPilesKeeper, "Timeout gettin player's ("+playerName+")piles");
               if (l==null) return null;
                return  waitingForPlayerPilesKeeper.getWaitingFor();
                
            }
        }

return null;
    }


      private SyncKeeper syncRequest(JoinedPlayer j, String m,SyncKeeper waitingFor,String message) {
        return syncRequest(j,m,waitingFor, 50,message);
    }


     private SyncKeeper syncRequest(JoinedPlayer j,String m,SyncKeeper waitingFor, int timeout, String message) {
      return AbstractNetworkGame.syncRequest(j,m,waitingFor, timeout, message, settings.getTemp().getLogger());
    }

    public void syncTable(String createSentence) {
       for (int i = 0; i < joindePlayers.size(); i++) {
             JoinedPlayer joinedPlayer = joindePlayers.get(i);
              joinedPlayer.messageToServer(syncing+": "+createSentence);
       }
    }

    public String getServerName() {
        return settings.getName();
    }

    public void shufflePile(String pile) {
          settings.getTemp().getPackage().sufflePile(pile.trim());
    }

    public void closeAll() {
        for (JoinedPlayer joinedPlayer : joindePlayers) {
            joinedPlayer.closeAll();

        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.networking.impl;

import cardmanager.networking.NetworkGame;
import cardmanager.networking.NetworkGameProceeder;
import cardmanager.impl.Settings;
import cardmanager.impl.card.Card;
import cardmanager.impl.networking.JoinedPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author Jirka
 */
class ServerNetworkGameProceeder implements NetworkGameProceeder {

    private ServerNetworkGame operator;
    private Settings settings;

    public ServerNetworkGameProceeder(ServerNetworkGame operator, Settings settings) {
        this.operator = operator;
        this.settings = settings;
    }

    public void proceedPlainText(String command, String s) {
        operator.messageToServerForAllImpl(command);
        settings.getTemp().getLogger().writeLocalMessage(s);
    }

    public void proceedRename(String s, Observable o) {
        String[] a = s.split(":");
        a[0] = a[0].trim();
        a[1] = a[1].trim();
        for (int i = 0; i < operator.joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = operator.joindePlayers.get(i);
            if (joinedPlayer.getName().equals(a[1])) {
                refuseReName(a[0], a[1], (JoinedPlayer) o);
                return;
            }
        }
        if (settings.getName().equals(a[1])) {
            refuseReName(a[0], a[1], (JoinedPlayer) o);
            return;
        }
        for (int i = 0; i < operator.joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = operator.joindePlayers.get(i);
            if (joinedPlayer.getName().equals(a[0])) {
                joinedPlayer.setName(a[1]);
            }
        }
        String aa = a[0] + " have changed name to" + a[1];
        operator.messageToServerForAllImpl(AbstractNetworkGame.renamed + ":" + a[0] + ":" + a[1]);
        settings.getTemp().getLogger().writeLocalMessage(aa);
        return;
    }

    private void refuseReName(String f, String t, JoinedPlayer back) {
        String aa = f + " have tried to rename himself to " + t + ", but was refused";
        operator.messageToServerForAllImpl(AbstractNetworkGame.plainTextForAll + ":" + aa);
        settings.getTemp().getLogger().writeLocalMessage(aa);

        back.messageToServer(AbstractNetworkGame.renameBack + ":" + f);
    }

    public void proceedRenameBack(String s) {
        //nothing to do
    }

    public void proceedSharedContent(String s, Observable o) {
        s = AbstractNetworkGame.parseSharedContent(s);
        settings.getTemp().getMemoWatcher().updateText(s);

        for (int i = 0; i < operator.joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = operator.joindePlayers.get(i);
            if (joinedPlayer != o) {
                joinedPlayer.messageToServer(AbstractNetworkGame.prepareSharedContent(s));
            }
        }

    }

    public void proceedNamesAllExceptMee(String s, Observable o) {
        List<String> l = new ArrayList(operator.joindePlayers.size());
        for (int i = 0; i < operator.joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = operator.joindePlayers.get(i);
            if (joinedPlayer != o) {
                l.add(joinedPlayer.getName());
            }


        }
        l.add(settings.getName());
        JoinedPlayer joi = (JoinedPlayer) o;
        joi.messageToServer(AbstractNetworkGame.prepareNames(l.toArray(new String[0])));


    }

    public void proceedNeedYourPiles(String name, JoinedPlayer p) {
        List<String> l = null;
        if (name.trim().equals(settings.getName().trim())) {
            l = settings.getTemp().getPackage().getPilesNamesWithCount();

        } else {
            String[] s = operator.getPlayerPiles(name);
            if (s == null) {
                s = new String[0];
            }
            l = Arrays.asList(s);
        }
        p.messageToServer(ServerNetworkGame.myPiles + ":" + AbstractNetworkGame.preparePiles(l));

    }

    public void proceedMyPiles(String s) {
        operator.waitingForPlayerPilesKeeper.setWaitingFor(AbstractNetworkGame.parsePiles(s));
    }

    public void allPlayersInOrder(String s, Observable o) {
        List<String> l = new ArrayList(operator.joindePlayers.size());
        l.add(settings.getName());
        for (int i = 0; i < operator.joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = operator.joindePlayers.get(i);

            l.add(joinedPlayer.getName());



        }

        JoinedPlayer joi = (JoinedPlayer) o;
        joi.messageToServer(AbstractNetworkGame.prepareAllNames(l.toArray(new String[0])));


    }

    public void syncTable(String s,Observable o) {
        if (s.trim().length() <= 1) {
            return;
        }
          for (int i = 0; i < operator.joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = operator.joindePlayers.get(i);
            if (joinedPlayer != o) {
                joinedPlayer.messageToServer(NetworkGame.syncing+":"+s);
            }
        }

        String[] q = s.split("&");
        if (q.length!=2) return;
        settings.getTemp().getGameViewOutput().remoteUpdate(q[1].split(";"), q[0]);
    }

    public void ask(String s, Observable object) {
           if (s.length()<1) return;
      String[] qq=s.split("&");
      if (qq.length<4) return;
      String nameFrom=qq[0];
      String nameTo=qq[1];
      String reqest=qq[2];
      String sPile=qq[3];
      String content="";
      if (qq.length>4) content=qq[4];

      if (nameTo.equals(settings.getName())){
      //for me, handle it
          JoinedPlayer jp=(JoinedPlayer) object;
          List<Card> pile=settings.getTemp().getPackage().getPile(sPile);
      if (reqest.equals(NetworkGame.LIST)){
          StringBuilder r=new StringBuilder();
          for (Card card : pile) {
              r.append(card.getSentence());
          }
          settings.getTemp().getNetwork().replyCards(settings.getName(),nameFrom,sPile,NetworkGame.RESPONSE,r.toString());
      }else      if (reqest.equals(NetworkGame.STEAL)){


          StringBuilder r=new StringBuilder();
          List<Card> cards=Card.cardsFromSentences(content, settings.getTemp().getCollection());
            for (Card card : cards) {
           if (pile.remove(card)) r.append(card.getSentence());
          }
          settings.getTemp().getNetwork().replyCards(settings.getName(),nameFrom,sPile,NetworkGame.RESPONSE,r.toString());
      }else      if (reqest.equals(NetworkGame.FROMTOP)){


             StringBuilder r=new StringBuilder();
             if (pile.size()>0){
           Card card=pile.get(0);//size-1?
           pile.remove(card);
           r.append(card.getSentence());

             }
          settings.getTemp().getNetwork().replyCards(settings.getName(),nameFrom,sPile,NetworkGame.RESPONSE,r.toString());
      }else      if (reqest.equals(NetworkGame.RESPONSE)){


            List<Card> cards=Card.cardsFromSentences(content, settings.getTemp().getCollection());

            operator.waitingForPlayerCardsKeeper.setWaitingFor(cards.toArray(new Card[cards.size()]));
      }



}else{
//for anybody else, just resends
   for (int i = 0; i < operator.joindePlayers.size(); i++) {
            JoinedPlayer joinedPlayer = operator.joindePlayers.get(i);
            if (joinedPlayer.getName().equals(nameTo)) {
                joinedPlayer.messageToServer(NetworkGame.ask+":"+s);
                System.out.println("resending ASK to"+nameTo);
            }


        }
}


}

    public void serverName(String s, Observable o) {
         JoinedPlayer joi = (JoinedPlayer) o;
        joi.messageToServer(NetworkGame.serverName+":"+settings.getName());
    }

    public void shufflePile(String pile) {
        settings.getTemp().getPackage().sufflePile(pile.trim());
    }



}
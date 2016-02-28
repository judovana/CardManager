/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.networking.impl;

import cardmanager.networking.NetworkGame;
import cardmanager.networking.NetworkGameProceeder;
import cardmanager.impl.Settings;
import cardmanager.impl.card.Card;
import cardmanager.impl.card.CardGeometry;
import cardmanager.impl.networking.JoinedPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author Jirka
 */
class ClientNetworkGameProceeder implements NetworkGameProceeder {

    private Settings settings;
    private ClientNetworkGame operator;

    public ClientNetworkGameProceeder(Settings settings, ClientNetworkGame operator) {
        this.operator = operator;
        this.settings = settings;
    }

    public void proceedPlainText(String command, String s) {
        settings.getTemp().getLogger().writeLocalMessage(s);
    }

    public void proceedRename(String s, Observable o) {
        String[] a = s.split(":");
        a[0] = a[0].trim();
        a[1] = a[1].trim();
        String aa = a[0] + " have changed name to" + a[1];
        settings.getTemp().getLogger().writeLocalMessage(aa);
    }

    public void proceedRenameBack(String s) {
        settings.setName(s);
    }

    public void proceedSharedContent(String s, Observable o) {
        s = AbstractNetworkGame.parseSharedContent(s);
        settings.getTemp().getMemoWatcher().updateText(s);
    }

    public void proceedNamesAllExceptMee(String s, Observable o) {
        operator.waitingForPlayersNamesKeeper.setWaitingFor(AbstractNetworkGame.prepareNames(s));
    }

    public void proceedNeedYourPiles(String n, JoinedPlayer p) {
        String s = AbstractNetworkGame.preparePiles(settings.getTemp().getPackage().getPilesNamesWithCount());
        operator.joinedPlayer.messageToServer(NetworkGame.myPiles + ":" + s);
    }

    public void proceedMyPiles(String s) {
        operator.waitingForPlayerPilesKeeper.setWaitingFor(AbstractNetworkGame.parsePiles(s));
    }

    public void allPlayersInOrder(String s, Observable o) {
        operator.waitingForPlayersNamesKeeper.setWaitingFor(AbstractNetworkGame.prepareAllNames(s));


    }

    public void syncTable(String s, Observable o) {
        if (s.trim().length() <= 1) {
            return;
        }
        String[] q = s.split("&");
        if (q.length != 2) {
            return;
        }
        settings.getTemp().getGameViewOutput().remoteUpdate(q[1].split(";"), q[0]);
    }

    public void ask(String s, Observable object) {
        if (s.length() < 1) {
            return;
        }
        String[] qq = s.split("&");
        if (qq.length < 4) {
            return;
        }
        String nameFrom = qq[0];
        String nameTo = qq[1];
        if (!settings.getName().equals(nameTo)) {
            System.out.println("warning - neme do not match expected name: " + settings.getName() + " x " + nameTo);
        }
        String reqest = qq[2];
        String sPile = qq[3];
        String content = "";
        if (qq.length > 4) {
            content = qq[4];
        }


        if (reqest.equals(NetworkGame.LIST)) {
            StringBuilder r = new StringBuilder();
            List<Card> pile = settings.getTemp().getPackage().getPile(sPile);
            for (Card card : pile) {
                r.append(card.getSentence());
            }
            settings.getTemp().getNetwork().replyCards(settings.getName(), nameFrom, sPile, NetworkGame.RESPONSE, r.toString());
        } else if (reqest.equals(NetworkGame.STEAL)) {



            StringBuilder r = new StringBuilder();
            List<Card> cards = Card.cardsFromSentences(content, settings.getTemp().getCollection());
            List<Card> pile = settings.getTemp().getPackage().getPile(sPile);
            for (Card card : cards) {
                if (pile.remove(card)) {
                    r.append(card.getSentence());
                }
            }
            settings.getTemp().getNetwork().replyCards(settings.getName(), nameFrom, sPile, NetworkGame.RESPONSE, r.toString());
        } else if (reqest.equals(NetworkGame.FROMTOP)) {

            List<Card> pile = settings.getTemp().getPackage().getPile(sPile);
            StringBuilder r = new StringBuilder();
            if (pile.size() > 0) {
                Card card = pile.get(0);//size-1?
                pile.remove(card);
                r.append(card.getSentence());

            }
            settings.getTemp().getNetwork().replyCards(settings.getName(), nameFrom, sPile, NetworkGame.RESPONSE, r.toString());
        } else if (reqest.equals(NetworkGame.RESPONSE)) {


            List<Card> cards = Card.cardsFromSentences(content, settings.getTemp().getCollection());

            operator.waitingForPlayerCardsKeeper.setWaitingFor(cards.toArray(new Card[cards.size()]));
        }

    }

    public void serverName(String s, Observable o) {
        operator.waitingForServerNamesKeeper.setWaitingFor(s.trim());
    }

    public void shufflePile(String pile) {
        //nothing to do :) but...
        settings.getTemp().getPackage().sufflePile(pile.trim());

    }
}

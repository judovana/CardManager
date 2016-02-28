/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.packages;

import cardmanager.networking.NetworkGame;
import cardmanager.impl.Settings;
import cardmanager.impl.card.Card;
import cardmanager.networking.impl.AbstractNetworkGame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jirka
 */
public class SharedPackage extends AbstractPackage{

    PackageDef serversPile=null;
    private Settings settings;
    private String serverName;

    public void setServersPile(PackageDef serversPile) {
        this.serversPile = serversPile;
    }


    
    public SharedPackage(Settings settings) {
while(this.serverName==null){
this.serverName=settings.getTemp().getNetwork().getServerName();
try{
    Thread.sleep(250);
}catch(Throwable t){
    t.printStackTrace();
}
    }
this.settings=settings;
    }

    private NetworkGame netork(){
        return settings.getTemp().getNetwork();
    }

    public Card getTopFrom(String id) {
        //check add to hand?
        if (settings.getImServer()){
            return serversPile.getTopFrom(id);
        } else{
    Card[] cards=netork().askCards(settings.getName(),serverName, id, netork().FROMTOP,  "");
    if (cards==null || cards.length<=0) return null;
    return cards[0];
        }
    }

  
    public Map<String, List<Card>> getPiles() {
        if (settings.getImServer()){
            return serversPile.getPiles();
        }else{
           throw  new UnsupportedOperationException();
        }
    }

    public List<Card> getPile(String id) {
        if (settings.getImServer()){
            return serversPile.getPile(id);
        }else{
            Card[] cc=netork().askCards(settings.getName(), serverName, id, NetworkGame.LIST, "");
            if (cc==null) return null;
            return Arrays.asList(cc);
        }
    }

    public List<String> getPilesNames() {
            if (settings.getImServer()){
            return serversPile.getPilesNames();
        }else{
                String[] q= netork().getPlayerPiles(serverName);
                if (q==null) return null;
                List<String> l=new ArrayList<String>(q.length);
                for (int i = 0; i < q.length; i++) {
                    String string = AbstractNetworkGame.getNameFromNameAndCount(q[i]);
                    l.add(string);
                }
            return l;
        }
    }

    public List<String> getPilesNamesWithCount() {
            if (settings.getImServer()){
            return serversPile.getPilesNamesWithCount();
        }else{
                String[] q= netork().getPlayerPiles(serverName);
                if (q==null) return null;
               return Arrays.asList(q);
        }
    }

    

    public void sufflePile(String pile) {
        if (settings.getImServer()){
            serversPile.sufflePile(pile);
        }else{
settings.getTemp().getNetwork().shufflePile(pile);
        }
    }



    public Card selectCard(Card c, String pile) {
            if (settings.getImServer()){
               List<Card> q = getPile(pile);
        if (q==null) return null;
            if (q.remove(c)) return c;else return null;
        }else{
          Card[] cc=netork().askCards(settings.getName(), serverName, pile, NetworkGame.STEAL, c.getSentence());
          if (cc==null || cc.length<=0) return null;
           return c;
        }
    }

   

}

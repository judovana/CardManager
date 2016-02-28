/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl;

import cardmanager.networking.NetworkGame;
import java.util.Observable;

/**
 *
 * @author Jirka
 */
public class NetworkLogger extends Observable{
    private Settings settings;
    private NetworkGame networkGame;

    public NetworkLogger(Settings settings) {
        this.settings=settings;
        settings.getTemp().setLoger(this);
    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);

    }

    public void renamedPlayer(String from, String to) {
        if (networkGame==null){
            writeLocalMessage(from+" have changed name to "+to);
        }else{
        networkGame.renamePlayer(from, to);
        }
    }

    public void setNetwork(NetworkGame result) {
       this.networkGame=result;
    }

    public void writeLocalMessage(String s){
        notifyObservers(s);
    }

    public void writeSharedMessage(String s){
        if (networkGame==null){
        notifyObservers(settings.getName()+": "+s);
        }else {
            networkGame.messageToServerForAll(s);;
        }
    }



}

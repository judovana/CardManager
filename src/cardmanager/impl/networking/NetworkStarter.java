/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.networking;

import cardmanager.networking.impl.ClientNetworkGame;
import cardmanager.networking.NetworkGame;
import cardmanager.networking.impl.ServerNetworkGame;
import cardmanager.impl.Settings;
import java.awt.Component;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author Jirka
 */
public class NetworkStarter {
    private Settings settings;

ServerPlayerCreator spc=null;
ClientPlayerCreator cpc=null;
ArrayList<JoinedPlayer> joinedPlayers=null;

    public ServerPlayerCreator getServer() {
        return spc;
    }



    public ArrayList<JoinedPlayer> getJoinedPlayers() {
        return joinedPlayers;
    }


    public NetworkStarter(Settings s) {
        this.settings=s;
    }

    public NetworkGame notifiedFinished() {
        return new ClientNetworkGame(settings, cpc.joinedPalyer);
    }

    public NetworkGame notifyFinished() {

        joinedPlayers=spc.getJoinedPlayers();
         for (JoinedPlayer jp:getJoinedPlayers()){
            try{
            jp.getOut().write("sccabbleXOWQ18gamestartFGH4463UIO\n");
            jp.getOut().flush();
            }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog((Component)null, ex);

            }
        }
         try{
         Thread.sleep(500);
         }catch(Exception ex){
             ex.printStackTrace();
         }

         return new ServerNetworkGame(settings, joinedPlayers);
    }


    public void start(DefaultListModel jpl,DefaultListModel cpl/*logs?*/,JDialog blockers,JDialog blockerc){

    if (settings.getImServer()){


    try{
    spc=new ServerPlayerCreator(jpl,settings);
    } catch (Exception ex) {
                    jpl.addElement("error connection was not created,restart");
                    ex.printStackTrace();
                    blockers.setVisible(true);
                }


    spc.start();
    blockers.setVisible(true);
    

    }
    else{
       
            cpc=null;
                try {
                    cpc = new ClientPlayerCreator(InetAddress.getByName(/*"localhost"/*/settings.getLastIp()), cpl,settings.getName(),blockerc,settings);
                } catch (Exception ex) {
                    cpl.addElement("error connection was not created. You can close the window");
                    ex.printStackTrace();
                    blockers.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                }
                if (cpc!=null){
                    cpc.start();
                                    }
           
                 blockerc.setVisible(true);

              
    }


    }
}

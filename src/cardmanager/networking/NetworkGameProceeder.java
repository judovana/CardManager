/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.networking;

import cardmanager.impl.networking.JoinedPlayer;
import java.util.Observable;

/**
 *
 * @author Jirka
 */
public interface NetworkGameProceeder {

    public void shufflePile(String pile);
    public void serverName(String s, Observable o);
    public void allPlayersInOrder(String s, Observable o);

    public void ask(String s, Observable object);

    public void proceedMyPiles(String s);

    public void proceedNamesAllExceptMee(String s, Observable o);

    public void proceedNeedYourPiles(String whois,JoinedPlayer fromwho);

    public void proceedPlainText(String command, String s);

    public void proceedRename(String s, Observable o);

     public void proceedRenameBack(String s) ;

    public void proceedSharedContent(String s, Observable o);

    public void syncTable(String s,Observable o);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.networking;

import cardmanager.impl.card.Card;


/**
 *
 * @author Jirka
 */
public interface NetworkGame {

  
public static final String plainTextForAll= "plainTextForAll";
public static final String renameBack= "renameBack";
public static final String renamed= "renamed";
public static final String sharedContent= "sharedContent";
public static final String namesAllExceptMee ="namesAllExceptMee";
public static final String allPlayersInOrder ="allPlayersInOrder";
public static final String needYourPiles ="needYourPiles";
public static final String myPiles ="myPiles";
public static final String syncing ="syncing";
public static final String serverName ="serverName";
public static final String shuffle ="shuffle";
public static final String ask ="ask";
///ask subtasks
public static final String LIST ="LIST";
public static final String FROMTOP ="FROMTOP";
public static final String STEAL ="STEAL";
public static final String RESPONSE ="RESPONSE";
///ask subtasks end

  public Card[] askCards(String nameFrom,String nameTo, String pile,String reqest,String context);
  public void replyCards(String nameFrom,String nameTo, String pile,String reqest,String context);

  public String getServerName();
  public String[] getPlayersExceptMe();
    public String[] getPlayersInOrder();
    public String[] getPlayerPiles(String playerName);

    public void messageToServerForAll(String string);
    public void renamePlayer(String from,String to);
    public void shufflePile(String pile);

    public void sendSharedContent(String s);

    public void syncTable(String createSentence);
    public void closeAll();


}

/*
 * ClientControler.java
 *
 * Created on 20. duben 2007, 20:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cardmanager.impl.networking;



import cardmanager.impl.packages.CountedCard;
import cardmanager.impl.packages.PackageDeffinition;
import cardmanager.impl.packages.PackageDeffinitionAndErrors;
import cardmanager.impl.packages.PackageVerifikationResult;
import cardmanager.impl.Settings;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;


/**
 *
 * @author Jirka
 */
public class ClientPlayerCreator  extends Thread{
   private  Socket socket;
   private  BufferedReader in;
   private  BufferedWriter out;
    private DefaultListModel output;
    private String name;

    private Settings settings;
    public JoinedPlayer joinedPalyer;
    private JDialog blockdialog;

    public Settings getSettings() {
        return settings;
    }

    /** Creates a new instance of ClientControler */
    public ClientPlayerCreator(InetAddress server,DefaultListModel output,String name,JDialog blocker,Settings settings) {
        this.blockdialog=blocker;
        this.settings=settings;
        this.output=output;
        this.name=name;


        try {
            socket=new Socket(server,settings.getLastPort());
              in = new BufferedReader(
                                 new InputStreamReader(
                                 socket.getInputStream()));
              out = new BufferedWriter(
                                 new OutputStreamWriter(
                                 socket.getOutputStream()));
              } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
        joinedPalyer=new JoinedPlayer(socket,in,out,name);

    }

    public void sentIdentification(String jmeno) throws IOException{
       out.write("sccabbleXOWQ18playernameFGH4463UIO-"+jmeno+"\r\n");
       out.flush();
       try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
       //String s=in.readLine();
    }

    public static void main(String args[])
    {
	}

   
    public BooleanAndMessage testAnswer() {
          try {

              String line;
               line = in.readLine();

             String s[]=line.trim().split("-");
             if (s.length>0){
                 if(s[0].equals("sccabbleXOWQ18playeracceptedFGH4463UIO")) return new BooleanAndMessage(true,s[1]);
                     else if(s[0].equals("sccabbleXOWQ18playerrefusedFGH4463UIO")){
                         return new BooleanAndMessage(false,s[1]);
                     }

                 }
          } catch (IOException ex) {
            ex.printStackTrace();
        }
          try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
              return new BooleanAndMessage(false,"connection error");

    }

    public void run() {
        try{
            output.addElement("connecting to server");
        sentIdentification(name);
                    BooleanAndMessage a=testAnswer();
                    if (!a.meaning){
                        output.addElement("you were refused: "+a.message);
                        blockdialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        //??joinedPalyer.closeAll();
                    }else{
                        output.addElement("you were accepted: "+a.message);
                        output.addElement("waiting for more info");
                       String is=in.readLine();
                       PackageDeffinitionAndErrors sharedP=null;
                       if (is.trim().equals("sccabbleXOWQ18sharedpileFGH4463UIO")){
                       StringBuilder sb=new StringBuilder("");

                       while(true){
                                is=in.readLine();
                               if (is==null) break;
                               if (is.trim().equals("sccabbleXOWQ18sharedpileendFGH4463UIO")) break;
                               sb.append(is+"\n");
                           }
                       sharedP=PackageDeffinition.createFromReaderAndCollection(new StringReader(sb.toString()), /*why??new CollectionOperator(0)*/ settings.getTemp().getCollection());
                       }else{
                       output.addElement("wrong invite sequention");
                       }
                       if (sharedP==null || sharedP.packageDef==null || sharedP.ex==null){
                           output.addElement("something unexpected arrived");
                       }else{
                           if (sharedP.packageDef.getCards().size()==0 && sharedP.packageDef.getPiles().size()==0 && sharedP.ex.size()==0){
                               output.addElement("shared pile will not be used");
                               settings.setSharedPile(false);
                           }else{
                               output.addElement("shared pile will be used: ");
                               settings.setSharedPile(true);
                               output.addElement("  cards: ");
                               for (CountedCard d : sharedP.packageDef.getCards()) {
                                   output.addElement("    "+d.toString());
                               }
                               output.addElement("  piles (+standart in!): ");
                               for (String d : sharedP.packageDef.getPiles()) {
                                   output.addElement("    "+d);
                               }
                               output.addElement(" result=> "+new PackageVerifikationResult(sharedP.packageDef).saySentence());

                               if (sharedP.ex.size()>0){
                                   output.addElement("ERRORS OCCURED WHILE LOADING: ");
                                   for(Exception ex:sharedP.ex){
                                       output.addElement(ex);
                                   }
                               }
                           }
                       }
                        
                   
                        try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                       
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                        output.addElement("necessary handshakes fnished");
        //treba prijmout bonzovanyu balicky ci tyak...
                        output.addElement("waiting for signal to start,please wait");
                        String s=in.readLine();
                        while(true){
                            if (s==null) break;
                            if (s.trim().length()>0) break;
                            s=in.readLine();
                        }
                        if (s.equals("sccabbleXOWQ18gamestartFGH4463UIO")){
                           output.addElement("game started");
                        }else{
                            output.addElement("wrong start!!! You can trye to continue...");
                        }
                        Thread.sleep(500);
                        blockdialog.setVisible(false);
                    }
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                    output.addElement("connecting error unknownhost");
                     blockdialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                } catch (Exception ex) {
                    output.addElement("connecting error io");
                    ex.printStackTrace();
                     blockdialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                }
    }






}

/*
 * ServerControler.java
 *
 * Created on 20. duben 2007, 20:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cardmanager.impl.networking;

import cardmanager.impl.Settings;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 *
 * @author Jirka
 */
public class ServerPlayerCreator extends Thread {

    ServerSocket socket;
    BufferedReader in;
    BufferedWriter out;
    Socket akcepted;
    private ArrayList<JoinedPlayer> joinedPlayers = new ArrayList();
    private DefaultListModel outputPlayers;
    private boolean finished = false;
    private Settings settings;

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public ArrayList<JoinedPlayer> getJoinedPlayers() {
        return joinedPlayers;
    }

    /** Creates a new instance of ServerControler */
    public ServerPlayerCreator(DefaultListModel outputPlayers, Settings settings) throws IOException {
        this.outputPlayers = outputPlayers;
        this.settings = settings;

        socket = new ServerSocket(settings.getLastPort());
        socket.setReuseAddress(true);


    }

    public void run() {

        try {

            while (!finished) {
                System.out.println("waitting");
                akcepted = socket.accept();
                in = new BufferedReader(
                        new InputStreamReader(
                        akcepted.getInputStream()));
                out = new BufferedWriter(
                        new OutputStreamWriter(
                        akcepted.getOutputStream()));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                String line = in.readLine();
                String s[] = line.trim().split("-");
                if (s.length > 0) {
                    if (s[0].equals("sccabbleXOWQ18playernameFGH4463UIO") && s.length > 1) {
                        System.out.println("player trying to join: " + s[1]);
                        boolean jetam = false;
                        //kontrola zdali tam je
                        if (s[1].equals(settings.getName())) {
                            jetam = true;
                        }
                        for (int i = 0; i < outputPlayers.getSize(); i++) {
                            if (outputPlayers.get(i).equals(s[1])) {
                                jetam = true;
                                break;
                            }
                        }
                        if (!jetam) {
                            JoinedPlayer jp = new JoinedPlayer(akcepted, in, out, s[1]);
                            joinedPlayers.add(jp);
                            if (outputPlayers != null) {
                                outputPlayers.addElement(jp.getName());
                            }
                            out.write("sccabbleXOWQ18playeracceptedFGH4463UIO-you have been accepted like: " + s[1] + "\r\n");
                            out.flush();
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            out.write("sccabbleXOWQ18sharedpileFGH4463UIO\n");
                            out.flush();
                            if (settings.getSharedPile()) {


                                settings.getTemp().getBattlePackage().save(out);
                                out.flush();
                            } else {
                                //empty content should be enough
                            }
                            out.write("\nsccabbleXOWQ18sharedpileendFGH4463UIO\n");
                            out.flush();

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
                            //out.flush();




                        } else {
                            out.write("sccabbleXOWQ18playerrefusedFGH4463UIO-you are already signed: " + s[1] + "\r\n");
                            out.flush();
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }



                }


            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
        }

        System.out.println("finished");


    }

    public static void main(String args[]) {
    }

    public void closeAll() {
        for (JoinedPlayer joinedPlayer : joinedPlayers) {
            joinedPlayer.closeAll();

        }
        try {
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            akcepted.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

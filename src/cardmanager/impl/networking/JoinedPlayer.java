/*
 * JoinedPlayer.java
 *
 * Created on 23. duben 2007, 10:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cardmanager.impl.networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Jirka
 */
public class JoinedPlayer extends Observable implements Runnable {

    private String name;
    private InetAddress ip;
    private BufferedWriter out;
    private BufferedReader in;
    private Socket socket;
    private Thread watchDog;

    public void startDog() {
        watchDog = new Thread(this);
        watchDog.start();
    }

    public BufferedReader getIn() {
        return in;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BufferedWriter getOut() {
        return out;
    }

    public Socket getSocket() {
        return socket;
    }

    /** Creates a new instance of JoinedPlayer */
    public JoinedPlayer(Socket socket, BufferedReader in, BufferedWriter out, String name) {
        this.socket = socket;
        this.ip = socket.getInetAddress();
        this.name = name;
        /*  try {
        in = new BufferedReader(
        new InputStreamReader(
        socket.getInputStream()));

        out = new BufferedWriter(
        new OutputStreamWriter(
        socket.getOutputStream()));
        } catch (IOException ex) {
        ex.printStackTrace();
        }*/
        this.in = in;
        this.out = out;


    }

    @Override
    public String toString() {
        return name;
    }

    public void messageToServer(String s) {
        System.out.println("Client out: " + s);
        try {
            getOut().write(s + "\n");
            getOut().flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                String s = in.readLine();
                if (s == null) {
                    return;
                }
                System.out.println("Client in: " + s);
                this.setChanged();
                notifyObservers(s);
            }catch (SocketException ex){
                ex.printStackTrace();
                        break;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void closeAll() {
        try {
            getOut().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            getIn().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            getSocket().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

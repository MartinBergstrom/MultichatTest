package Server.Clientconnections;

import Server.ServerGUI;

import java.io.*;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-14.
 */
public class AbstractClientConnection {
    protected Socket socket;
    protected boolean disconnected = false;
    protected DataInputStream dis;
    protected DataOutputStream dos;

    protected ServerGUI gui;

    public AbstractClientConnection(Socket sock, ServerGUI gui){
        this.socket = sock;
        this.gui = gui;
        setUpConnection();
    }


    private void setUpConnection(){
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        disconnected = true;
    }

    public boolean getDisconnect(){
        return disconnected;
    }
}

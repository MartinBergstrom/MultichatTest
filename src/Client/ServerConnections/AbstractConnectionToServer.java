package Client.ServerConnections;

import Client.GUI.ClientGUI;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Martin on 2017-05-13.
 *
 * Abstract class for a client connection. Sets up the socket and the Data(Input/Output)Streams
 */
public abstract class AbstractConnectionToServer {
    protected String ipAddress;
    protected ClientGUI gui;
    protected String clientName; //this will be the clients name for this particular connection

    protected int port;
    protected Socket socket;

    protected DataInputStream dis;
    protected DataOutputStream dos;

    protected boolean disconnected = false;

    public AbstractConnectionToServer(String ipAddress, int port, ClientGUI gui) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.gui = gui;
        setUpConnection();
    }

    private void setUpConnection(){
        try{
            socket = new Socket(ipAddress,port);
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);
            clientName = socket.getLocalSocketAddress().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientName(){
        return clientName;
    }
    public void disconnect(){
        disconnected = true;
    }

    public abstract void sendClientInfo(String clientName);
}

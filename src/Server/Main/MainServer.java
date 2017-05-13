package Server.Main;

import Server.Clientconnections.ClientMessageConnection;
import Server.HandleNewClients.HandleNewClientConnections;
import Server.ServerGUI;

import java.awt.image.BufferedImage;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Martin on 2017-05-08.
 */
public class MainServer {
    private static String hostname;
    private int port;
    private List<ClientMessageConnection> connections;
    private ServerGUI gui;


    public MainServer(int port, ServerGUI gui){
        this.port = port;
        this.gui=gui;
        connections = new ArrayList<>();
        try {
            hostname = Inet4Address.getLocalHost().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        startHandlerThread();
        gui.disableActive();
    }

    /**
     * Starts the handler thread responsible for listening to new client connections
     */
    private void startHandlerThread() {
        new Thread(new HandleNewClientConnections(gui,port,this)).start();

    }

    public synchronized void addConnection(ClientMessageConnection cct){
        connections.add(cct);
    }

    public synchronized boolean broadCastMessage(String message){
        boolean finished = true;
        Iterator<ClientMessageConnection> itr = connections.iterator();
        while (itr.hasNext()){
            ClientMessageConnection cct = itr.next();
                if(!cct.sendMessage(message)){
                    finished = false;
                    break;
                }
        }
        return finished;
    }
    public synchronized boolean broadcastImage(BufferedImage image, String imageType){
        boolean finished = true;
        Iterator<ClientMessageConnection> itr = connections.iterator();
        System.out.println("connection.size is: "+connections.size());
        while (itr.hasNext()){
            ClientMessageConnection cct = itr.next();
            if(!cct.sendPicture(image,imageType)){
                finished = false;
                break;
            }
        }
        return finished;
    }

    public synchronized boolean isConnectionsEmpty(){
        return connections.size() == 0? true: false;
    }

    public synchronized void checkConnections(){
        if(connections.size()>0){
            Iterator<ClientMessageConnection> itr = connections.iterator();
            while(itr.hasNext()){
                ClientMessageConnection cct = itr.next();
                if(cct.isDisconnected()){
                    itr.remove();
                }
            }
        }
    }

    public synchronized int getNumberOfConnections(){
        return connections.size();
    }


    public String getHostname(){
        return hostname;
    }

    /**
     * You can choose to run the program with gui or just console
     * Just enter port for console, and for gui enter port and something else, example " 6532 gui"
     *
     * @param args port and write anything else for gui
     */
    public static void main(String[] args) {
        MainServer msh = null;
            ServerGUI gui = new ServerGUI();
            msh = new MainServer(Integer.parseInt(args[0]), gui);
            gui.addServer(msh);
    }
}

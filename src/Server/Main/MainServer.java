package Server.Main;

import Server.Clientconnections.ClientFileConnection;
import Server.Clientconnections.ClientImageConnection;
import Server.Clientconnections.ClientMessageConnection;
import Server.Clientconnections.Connections;
import Server.HandleNewClients.HandleNewClientConnections;
import Server.GUI.ServerGUI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Martin on 2017-05-08.
 */
public class MainServer{
    private static String hostname;
    private int port;
    private List<Connections> allConnections;
    private List<Connections> activeConnections;

    private ServerGUI gui;

    public MainServer(int port, ServerGUI gui){
        this.port = port;
        this.gui=gui;

        allConnections = new ArrayList<>();
        activeConnections = new ArrayList<>();
        try {
            hostname = Inet4Address.getLocalHost().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        startHandlerThread();
        gui.disableActive();
    }

    /**
     * Starts the handler thread responsible for listening to new client allConnections
     */
    private void startHandlerThread() {
        new Thread(new HandleNewClientConnections(gui,port,this)).start();

    }

    public synchronized void addConnection(Connections connections){
        allConnections.add(connections);
        gui.addConnectionToList(connections); //add to gui
    }

    public synchronized boolean sendMessage(String message){
        boolean finished = true;
        Iterator<Connections> itr = activeConnections.iterator();
        while (itr.hasNext()){
            ClientMessageConnection cct = itr.next().getCmc();
            if(!cct.sendMessage(message)){
                finished = false;
                break;
            }
        }
        return finished;
    }

    public synchronized boolean sendImage(BufferedImage image, String imageType){
        Iterator<Connections> itr = activeConnections.iterator();
        while (itr.hasNext()){
            ClientImageConnection cct = itr.next().getCic();
            cct.sendImage(image,imageType);
        }
        return true;
    }

    public synchronized boolean sendFile(File file){
        Iterator<Connections> itr = activeConnections.iterator();
        while (itr.hasNext()){
            ClientFileConnection cct = itr.next().getCfc();
            cct.sendFile(file);
        }
        return true;
    }

    public synchronized boolean isConnectionsEmpty(){
        return allConnections.size() == 0? true: false;
    }

    public synchronized void checkConnections(){
        if(allConnections.size()>0){
            Iterator<Connections> itr = allConnections.iterator();
            while(itr.hasNext()){
                Connections cct = itr.next();
                if(cct.getDisconnect()){
                    itr.remove();
                    gui.removeConnectionFromList(cct);
                }
            }
        }
    }

    public synchronized void setSelectedValues(List<Connections> selectedValues) {
        activeConnections = new ArrayList<>(selectedValues);
    }

    public synchronized int getNumberOfConnections(){
        return allConnections.size();
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

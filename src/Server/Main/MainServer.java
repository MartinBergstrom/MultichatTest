package Server.Main;

import Server.Clientconnections.ClientMessageConnection;
import Server.GUI.ListOfConnections;
import Server.HandleNewClients.HandleNewClientConnections;
import Server.GUI.ServerGUI;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Martin on 2017-05-08.
 */
public class MainServer{
    private static String hostname;
    private int port;
    private List<ClientMessageConnection> allConnections;
    private List<ClientMessageConnection> activeConnections;

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

    public synchronized void addConnection(ClientMessageConnection cct){
        allConnections.add(cct);
        gui.addConnectionToList(cct); //add to gui
    }

    public synchronized boolean sendMessage(String message){
        boolean finished = true;
        Iterator<ClientMessageConnection> itr = activeConnections.iterator();
        while (itr.hasNext()){
            ClientMessageConnection cct = itr.next();
            if(!cct.sendMessage(message)){
                finished = false;
                break;
            }
        }
        return finished;
    }

    public synchronized boolean sendImage(BufferedImage image, String imageType){
        Iterator<ClientMessageConnection> itr = activeConnections.iterator();
        while (itr.hasNext()){
            ClientMessageConnection cct = itr.next();
            cct.sendPicture(image,imageType);
        }
        return true;
    }

    public synchronized boolean sendFile(File file){
        Iterator<ClientMessageConnection> itr = activeConnections.iterator();
        while (itr.hasNext()){
            ClientMessageConnection cct = itr.next();
            cct.sendFile(file);
        }
        return true;
    }

    public synchronized boolean isConnectionsEmpty(){
        return allConnections.size() == 0? true: false;
    }

    public synchronized void checkConnections(){
        if(allConnections.size()>0){
            Iterator<ClientMessageConnection> itr = allConnections.iterator();
            while(itr.hasNext()){
                ClientMessageConnection cct = itr.next();
                if(cct.getDisconnect()){
                    itr.remove();
                    gui.removeConnectionFromList(cct);
                }
            }
        }
    }

    public synchronized void setSelectedValues(List<ClientMessageConnection> selectedValues) {
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

package Server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Martin on 2017-05-08.
 */
public class MainServerHandler {
    private ServerSocket serverSocket;
    private static String hostname;
    private Socket socket;
    private int port;
    private List<ClientConnection> connections;
    private boolean consoleMode;
    private ServerGUI gui;

    public MainServerHandler(int port){
        this.port = port;
        connections = new ArrayList<>();
        consoleMode = true;
        startHandlerThread();
    }

    public MainServerHandler(int port, ServerGUI gui){
        this.port = port;
        this.gui=gui;
        connections = new ArrayList<>();
        consoleMode = false;
        startHandlerThread();
    }

    private void startHandlerThread() {
        try {
            serverSocket = new ServerSocket(port);
            hostname = Inet4Address.getLocalHost().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(consoleMode){
            new Thread(new ConsoleHandlerThread(socket,serverSocket,this)).start();
        }else{
            new Thread(new GUIHandlerThread(gui,socket,serverSocket,this)).start();
        }
    }

    public synchronized void addConnection(ClientConnection cct){
        connections.add(cct);
    }

    public synchronized boolean broadCastMessage(String message){
        boolean finished = true;
        checkConnections();
        if(connections.size() == 0){
            gui.disableAbleToType();
        }
        Iterator<ClientConnection> itr = connections.iterator();
        while (itr.hasNext()){
            ClientConnection cct = itr.next();
                if(!cct.sendMessage(message)){
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
            Iterator<ClientConnection> itr = connections.iterator();
            while(itr.hasNext()){
                ClientConnection cct = itr.next();
                if(cct.isDisconnected()){
                    itr.remove();
                }
            }
        }
    }

    public synchronized void newConsoleWriterIfEmpty() {
        if (connections.size() == 0) {
            new Thread(new MainServerHandler.ConsoleToClientWriter()).start();
        }
    }

    class ConsoleToClientWriter implements Runnable{
        @Override
        public void run() {
            Scanner scan = new Scanner(System.in);
            while (true){
                String message = scan.nextLine();
                if(message==null){
                    return;
                }
                message = gui.modifyMessage(message, "SERVER", hostname);
                broadCastMessage(message);
            }
        }
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
        MainServerHandler msh = null;
        if(args.length>1){
            ServerGUI gui = new ServerGUI();
            msh = new MainServerHandler(Integer.parseInt(args[0]), gui);
            gui.addServer(msh);
        }else {
            msh = new MainServerHandler(Integer.parseInt(args[0]));
        }
    }
}

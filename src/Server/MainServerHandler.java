package Server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Martin on 2017-05-08.
 */
public class MainServerHandler {
    private ServerSocket serverSocket;
    private static String hostname;
    private Socket socket;
    private int port;
    private List<ClientConnectionThread> connections;

    private ServerGUI gui;

    public MainServerHandler(int port){
        this.port = port;
        connections = new ArrayList<>();
        startHandlerThread();
    }

    public MainServerHandler(int port, ServerGUI gui){
        this.port = port;
        this.gui=gui;
        connections = new ArrayList<>();
        startHandlerThread();
    }

    private void startHandlerThread(){
        try{
            serverSocket = new ServerSocket(port);
            hostname = Inet4Address.getLocalHost().toString();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        System.out.println("Waiting for clients to connect...");
                        try {
                            socket = serverSocket.accept();
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        //check if old clients are disconnected, and in that case, remove them
                        checkConnections();

                        ClientConnectionThread cct = new ClientConnectionThread(socket,gui);
                        Thread t = new Thread(cct);
                        t.start();
                        connections.add(cct);
                        System.out.println("A new client at IP address: " + socket.getInetAddress().getHostName() + " connected to the server");
                    }
                }
            };
            new Thread(r).start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean broadCastMessage(String message){
        boolean finished = true;
        Iterator<ClientConnectionThread> itr = connections.iterator();
        while (itr.hasNext()){
            ClientConnectionThread cct = itr.next();
            if(!cct.sendMessage(message)){
                finished = false;
                break;
            }
        }
        return finished;
    }

    public void checkConnections(){
        if(connections.size()>0){
            Iterator<ClientConnectionThread> itr = connections.iterator();
            while(itr.hasNext()){
                ClientConnectionThread cct = itr.next();
                if(cct.isDisconnected()){
                    itr.remove();
                }
            }
        }
    }

    public static String modidfyMessage(String message){
        return "SERVER: " + hostname + " - " + message;
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

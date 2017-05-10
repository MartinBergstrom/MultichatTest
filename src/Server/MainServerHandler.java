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
                        //this is a problem cus I if I write after the last client disconnected it crashes
                        checkConnections();

                        ClientConnection cct = null;
                        if(consoleMode){
                            cct = new ClientConnection(socket);
                            if(connections.size()==0){
                                new Thread(new ConsoleToClientWriter()).start();
                            }
                            System.out.println("A new client at IP address: " + hostname +
                                    " connected to the server");
                        }else{
                            cct = new ClientConnection(socket,gui);
                            gui.updateMessageToTextArea("A new client at IP address: " +hostname +
                                    " connected to the server");
                        }
                        connections.add(cct);
                    }
                }
            };
            new Thread(r).start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public synchronized boolean broadCastMessage(String message){
        boolean finished = true;
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

    public void checkConnections(){
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

    public static String modidfyMessage(String message){
        return "SERVER: " + hostname + " - " + message;
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
                message = modidfyMessage(message);
                if( !broadCastMessage(message)){
                    System.out.println("vafan kunde ej broadcasta nu");
                }else{
                    System.out.println("kunde brodcasta iaf?");
                }
            }
        }
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

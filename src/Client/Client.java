package Client;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Martin on 2017-05-09.
 */
public class Client{
    private static String host;
    private static int port;
    private static String hostname;
    private InputStream is;
    private OutputStream os;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    private ClientGUI gui;

    public Client(String host, int port){
        this.host = host;
        this.port = port;
        setUpConnection();
    }

    public Client(String host, int port, ClientGUI gui){
        this.host = host;
        this.port = port;
        this.gui = gui;
        setUpConnection();
    }

    private void setUpConnection(){
        try {
            socket = new Socket(host, port);
            is = socket.getInputStream();
            os = socket.getOutputStream();

            reader = new BufferedReader(new InputStreamReader(is));
            writer = new BufferedWriter(new OutputStreamWriter(os));

            hostname = Inet4Address.getLocalHost().toString();
        } catch (IOException e) {
            System.err.println("Could not connect to server");
            System.exit(0);
        }
        System.out.println("Client is up and connected to the server");
        if(gui!=null){
            gui.enableAbleToType();
        }
    }

    public void startThread(){
        new Thread(new ServerReader()).start();
    }

    public void startConsoleToServerThread(){
        new Thread(new ConsoleToServerWriter()).start();
    }

    public boolean sendMessage(String message){
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            gui.updateMessageToTextArea("Something went wrong, couldn't send the message");
            return false;
        }
        return true;
    }

    public static String modifyMessage(String message){
        return "CLIENT: " + hostname + " - " + message;
    }

    class ConsoleToServerWriter implements Runnable{
        @Override
        public void run() {
            Scanner scan = new Scanner(System.in);
            while(true){
                String message = scan.nextLine();
                message = modifyMessage(message);
                sendMessage(message);
            }
        }
    }

    class ServerReader implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String message = reader.readLine();
                    if (message.equalsIgnoreCase("SERVER - END")) {
                        break;
                    }
                    if(gui != null){
                        gui.updateMessageToTextArea(message);
                    }else{
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                        is.close();
                        os.close();
                        System.exit(0);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Starts the main Client, you can choose to start with gui or run in console
     *
     * @param args host, port and write anything else for gui
     */
    public static void main(String[] args){
        Client c = null;
        if(args.length >2 ){
            System.out.println(args[0] + " " + args[1]);
            ClientGUI gui = new ClientGUI();
            c = new Client(args[0], Integer.parseInt(args[1]), gui);
            gui.addClient(c);
        }else{
            c = new Client(args[0], Integer.parseInt(args[1]));
            c.startConsoleToServerThread();
        }
        c.startThread();
    }
}

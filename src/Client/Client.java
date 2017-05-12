package Client;

import HandleDataTransfer.PictureHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;

    private ClientGUI gui;

    public Client(String host, int port){
        this.host = host;
        this.port = port;
        setUpConnection();
        System.out.println("Client is up and connected to the server");
    }

    public Client(String host, int port, ClientGUI gui){
        this.host = host;
        this.port = port;
        this.gui = gui;
        setUpConnection();
        gui.updateMessageToTextArea("--- You're now connected to server at IP: " + host + " ---");
        gui.enableActive();
    }

    private void setUpConnection(){
        try {
            gui.updateMessageToTextArea("Establishing connection to server... ");
            socket = new Socket(host, port);
            is = socket.getInputStream();
            os = socket.getOutputStream();

            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);

            hostname = Inet4Address.getLocalHost().toString();
        } catch (IOException e) {
            System.err.println("Could not connect to server");
            System.exit(0);
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
            dos.writeUTF("msg");
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            if(gui!=null){
                gui.updateMessageToTextArea("Something went wrong, couldn't send the message");
            }else{
                System.out.println("Something went wrong, couldn't send the message");
            }
            return false;
        }
        return true;
    }

    public boolean sendPicture(BufferedImage img, String imageType){
        return PictureHandler.sendPicture(img,imageType,dos);
    }

    class ConsoleToServerWriter implements Runnable{
        @Override
        public void run() {
            Scanner scan = new Scanner(System.in);
            while(true){
                String message = scan.nextLine();
                message = gui.modifyMessage(message, "CLIENT", hostname);
                sendMessage(message);
            }
        }
    }


    class ServerReader implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String header = dis.readUTF();
                    if(header.equals("pic")){
                        gui.updateMessageToTextArea("--- Picture from server recieved: ---");
                        BufferedImage img = PictureHandler.retrievePicture(dis);
                        if(img!=null){
                            gui.showImage(img);
                        }else{
                            System.out.println("Image was null :(");
                        }
                    }else if(header.equals("msg")){
                        String message = dis.readUTF();
                        if(gui != null){
                            gui.updateMessageToTextArea(message);
                        }else{
                            System.out.println(message);
                        }
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

    public String getHostName(){
        return hostname;
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

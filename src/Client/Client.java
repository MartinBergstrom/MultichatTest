package Client;

import Client.Images.ImageHandlerClient;
import HandleDataTransfer.ImageHandler;

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

    private ImageHandlerClient imageClient;

    public Client(String host, int port, ClientGUI gui){
        this.host = host;
        this.port = port;
        this.gui = gui;
        setUpConnection();
        gui.updateMessageToTextArea("--- You're now connected to server at IP: " + host + " ---");
        gui.enableActive();

        imageClient = new ImageHandlerClient(host,4800,gui); //use port 4800 for images
    }

    private void setUpConnection(){
        try {
            gui.updateMessageToTextArea("Establishing connection to server... ");
            socket = new Socket(host, port);
            is = socket.getInputStream();
            os = socket.getOutputStream();

            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);

            new Thread(new ServerReader()).start();

            hostname = Inet4Address.getLocalHost().toString();
        } catch (IOException e) {
            System.err.println("Could not connect to server");
            System.exit(0);
        }
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
        return imageClient.sendImage(img,imageType);
    }


    class ServerReader implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String header = dis.readUTF();
                    if(header.equals("msg")){
                        String message = dis.readUTF();
                        gui.updateMessageToTextArea(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    gui.updateMessageToTextArea("Lost connection to server, shutting down... ");
                    try {
                        Thread.sleep(2500);
                        socket.close(); //this also closes is and os
                        imageClient.disconnect();
                        System.exit(0);
                    } catch (IOException e1) {}
                      catch (InterruptedException e1) {}
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
        System.out.println(args[0] + " " + args[1]);
        ClientGUI gui = new ClientGUI();
        c = new Client(args[0], Integer.parseInt(args[1]), gui);
        gui.addClient(c);
    }
}

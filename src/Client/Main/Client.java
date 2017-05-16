package Client.Main;

import Client.GUI.ClientGUI;
import Client.ServerConnections.FileHandlerClient;
import Client.ServerConnections.ImageHandlerClient;
import Client.ServerConnections.MessageHandlerClient;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-09.
 */
public class Client{
    private static String host;
    private static int port;
    private String clientName;

    private ClientGUI gui;

    private MessageHandlerClient messageClient;
    private ImageHandlerClient imageClient;
    private FileHandlerClient fileClient;

    public Client(String host, int port, ClientGUI gui){
        this.host = host;
        this.port = port;
        this.gui = gui;

        setUpConnections();
        gui.enableActive();
    }

    private void setUpConnections(){
        gui.updateMessageToTextArea("Establishing connection to server at IP: " + host + " ... ");
        messageClient = new MessageHandlerClient(host,port,gui); //this will set the clientName
        clientName = messageClient.getClientName();
        System.out.println(clientName);
        messageClient.sendClientInfo(clientName);

        imageClient = new ImageHandlerClient(host,port,gui);
        imageClient.sendClientInfo(clientName);

        fileClient = new FileHandlerClient(host,port,gui);
        fileClient.sendClientInfo(clientName);
        gui.updateMessageToTextArea("Connected to server at IP: " + host);
    }

    public boolean sendMessage(String message){
       return messageClient.sendMessage(message);
    }

    public void sendPicture(BufferedImage img, String imageType){
        imageClient.sendImage(img,imageType);
    }

    public void sendFile(File file){
        fileClient.sendFile(file);
    }

    public String getHostName(){
        return clientName;
    }

    /**
     * Starts the main Client, you can choose to start with gui or run in console
     *
     * @param args host, port and write anything else for gui
     */
    public static void main(String[] args){
        Client c = null;
        //System.out.println(args[0] + " " + args[1]);
        ClientGUI gui = new ClientGUI();
        //c = new Client(args[0], Integer.parseInt(args[1]), gui);
        c = new Client("127.0.0.1",6532,gui);
        gui.addClient(c);
    }
}

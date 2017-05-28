package Client.GUI;

import Client.Main.Client;
import GUITemplate.GUITemplate1;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Martin on 2017-05-08.
 *
 *
 */
public class ClientGUI extends GUITemplate1 {
    private Client client;

    public ClientGUI(){
        super("Client");
//        String hostname = JOptionPane.showInputDialog(this,"Enter server IP:");
//        int port = Integer.parseInt(JOptionPane.showInputDialog(this,"Enter port number"));
//        client = new Client(hostname,port,this);
        client = new Client("127.0.0.1", 6532,this);
    }

    @Override
    public void sendMessage(String message) {
        if(ableToType) {
            message = super.modifyMessage(message, "CLIENT", client.getHostName());
            if (client.sendMessage(message)) {
                updateMessageToTextArea(message);
            }
        }
    }

    @Override
    public void sendPicture(BufferedImage img , String imgType) {
        client.sendPicture(img,imgType);
        insertNewLine();
        updateMessageToTextArea("--- Successfully sent the picture to server ---");
    }

    @Override
    public void sendFile(File file) {
        client.sendFile(file);
    }
}
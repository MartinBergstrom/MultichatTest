package Client.GUI;

import Client.Main.Client;
import GuiTemplate.GUITemplate1;

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
    }

    public void addClient(Client c){
        this.client = c;
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
        updateMessageToTextArea("");
    }

    @Override
    public void sendFile(File file) {
        client.sendFile(file);
    }
}
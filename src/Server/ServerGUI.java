package Server;

import GuiTemplate.GUITemplate1;
import Server.Main.MainServer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Martin on 2017-05-09.
 */
public class ServerGUI extends GUITemplate1 {
    private MainServer server;
    private JLabel nbrC; //displays the number of current client connections

    public ServerGUI(){
        super("Server");
        JLabel nbrCLabel = new JLabel("Nbr of connections:");
        nbrCLabel.setFont(new Font("Serif", Font.BOLD,10));

        nbrC = new JLabel("0");
        nbrC.setFont(new Font("Serif", Font.BOLD,10));
        messagePanel.add(nbrCLabel);
        messagePanel.add(nbrC);

    }
    public void addServer(MainServer server){
        this.server = server;
    }

    @Override
    public void sendMessage(String message) {
        message = super.modifyMessage(message, "SERVER", server.getHostname());
        if(server.broadCastMessage(message)){
            updateMessageToTextArea(message);
        }
    }

    @Override
    public boolean sendPicture() {
        if(server.broadcastImage(img, imgType)){
            updateMessageToTextArea("\n--- Picture was successfully broadcasted to the clients ---");
            return true;
        }
        updateMessageToTextArea("\n--- Could not broadcast the image, error occured ---");
        return false;
    }

    //smells bad? criss cross reference
    public void checkConnections(){
        server.checkConnections();
        if(server.isConnectionsEmpty()){
            disableActive();
        }else{
            enableActive();
        }
        setNumberOfConnections(server.getNumberOfConnections());
    }

    public void setNumberOfConnections(int nbr){
        nbrC.setText(Integer.toString(nbr));
    }
}
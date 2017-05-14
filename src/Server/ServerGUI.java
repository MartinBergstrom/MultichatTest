package Server;

import GuiTemplate.GUITemplate1;
import Server.Main.MainServer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

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

        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(110,300));
        JLabel text = new JLabel("Connected clients: ");
        text.setFont(new Font("Serif", Font.BOLD,12));
        sidePanel.add(text);
        JTextArea test1 = new JTextArea("h1dasda \n hahfahfah");
        sidePanel.add(new JScrollPane(test1));
        add(sidePanel,BorderLayout.EAST);

        setSize(new Dimension(520,300));

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
    public void  sendPicture(BufferedImage img, String imgType) {
        if(server.broadcastImage(img, imgType)){
            updateMessageToTextArea("\n--- Picture was successfully broadcasted to the clients ---");
        }else{
            updateMessageToTextArea("\n--- Could not broadcast the image, error occured ---");
        }
    }

    @Override
    public void sendFile(File file) {

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
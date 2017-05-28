package Server.GUI;

import GUITemplate.GUITemplate1;
import Server.Clientconnections.Connections;
import Server.Main.MainServer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Martin on 2017-05-09.
 */
public class ServerGUI extends GUITemplate1 implements ActionListener,ListSelectionListener {
    private MainServer server;
    private JLabel nbrC; //displays the number of current client connections
    private ListOfConnections listOfConnections;

    public ServerGUI(){
        super("Server");
        setLocation(new Point(600,80));
        JLabel nbrCLabel = new JLabel("Nbr of connections:");
        nbrCLabel.setFont(new Font("Serif", Font.BOLD,10));

        nbrC = new JLabel("0");
        nbrC.setFont(new Font("Serif", Font.BOLD,10));
        messagePanel.add(nbrCLabel);
        messagePanel.add(nbrC);

        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(170,350));
        JLabel text = new JLabel("Connected clients: ");
        text.setFont(new Font("Serif", Font.BOLD,12));
        sidePanel.add(text);
        listOfConnections = new ListOfConnections();
        listOfConnections.addListSelectionListener(this);
        listOfConnections.addBroadcastListener(this);
        sidePanel.add(listOfConnections);
        backgroundPanel.add(sidePanel,BorderLayout.EAST);

        disableActive();
        setSize(new Dimension(700,370));

    }
    public void addServer(MainServer server){
        this.server = server;
    }

    @Override
    public void sendMessage(String message) {
        message = super.modifyMessage(message, "SERVER", server.getHostname());
        if(server.sendMessage(message)){
            updateMessageToTextArea(message);
        }
    }


    @Override
    public void sendPicture(BufferedImage img, String imgType) {
        if(server.sendImage(img, imgType)){
            insertNewLine();
            updateMessageToTextArea("--- Picture was successfully sent to the selected clients ---");
        }else{
            updateMessageToTextArea("--- Could not send the image, error occurred ---");
        }
    }

    @Override
    public void sendFile(File file) {
        server.sendFile(file);
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

    public void addConnectionToList(Connections connections){
        System.out.println("trying to add the new connection in gui list");
        listOfConnections.addConnection(connections);
    }
    public void removeConnectionFromList(Connections connections){
        listOfConnections.removeConnection(connections);
    }

    public void setNumberOfConnections(int nbr){
        nbrC.setText(Integer.toString(nbr));
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()){
            enableActive();
            final List<Connections> selectedValues = listOfConnections.getList().getSelectedValuesList();
            server.setSelectedValues(selectedValues);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JList list = listOfConnections.getList();
        if(listOfConnections.getList().getSelectedIndices().length < listOfConnections.getList().getModel().getSize()){
            int start = 0;
            int end = list.getModel().getSize() - 1;
            if (end >= 0) {
                list.setSelectionInterval(start, end);
            }
        }else{
            list.clearSelection();
            disableActive();
        }
    }
}
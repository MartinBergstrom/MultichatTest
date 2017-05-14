package Server.GUI;

import GuiTemplate.GUITemplate1;
import Server.Clientconnections.ClientMessageConnection;
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
        JLabel nbrCLabel = new JLabel("Nbr of connections:");
        nbrCLabel.setFont(new Font("Serif", Font.BOLD,10));

        nbrC = new JLabel("0");
        nbrC.setFont(new Font("Serif", Font.BOLD,10));
        messagePanel.add(nbrCLabel);
        messagePanel.add(nbrC);

        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(170,300));
        JLabel text = new JLabel("Connected clients: ");
        text.setFont(new Font("Serif", Font.BOLD,12));
        sidePanel.add(text);
        listOfConnections = new ListOfConnections();
        listOfConnections.addListSelectionListener(this);
        listOfConnections.addBroadcastListener(this);
        sidePanel.add(listOfConnections);
        add(sidePanel,BorderLayout.EAST);

        disableActive();
        setSize(new Dimension(600,300));

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
    public void  sendPicture(BufferedImage img, String imgType) {
        if(server.sendImage(img, imgType)){
            updateMessageToTextArea("\n--- Picture was successfully broadcasted to the clients ---");
        }else{
            updateMessageToTextArea("\n--- Could not broadcast the image, error occured ---");
        }
    }

    @Override
    public void sendFile(File file) {
        if(server.sendFile(file)){
            updateMessageToTextArea("\n--- File was successfully broadcasted to the clients ---");
        }else{
            updateMessageToTextArea("\n--- Could not broadcast the file, error occured ---");
        }
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

    public void addConnectionToList(ClientMessageConnection cmc){
        listOfConnections.addConnection(cmc);
    }
    public void removeConnectionFromList(ClientMessageConnection cmc){
        listOfConnections.removeConnection(cmc);
    }

    public void setNumberOfConnections(int nbr){
        nbrC.setText(Integer.toString(nbr));
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()){
            enableActive();
            final List<ClientMessageConnection> selectedValues = listOfConnections.getList().getSelectedValuesList();
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
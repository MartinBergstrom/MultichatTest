package Server.GUI;


import Server.Clientconnections.ClientMessageConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.BorderLayout;

/**
 * Created by Martin on 2017-05-14.
 */
public class ListOfConnections extends JPanel {
    private DefaultListModel<ClientMessageConnection> listModel;
    private JList<ClientMessageConnection> list;
    private JButton broadcastButton;

    public ListOfConnections(){
        setLayout(new BorderLayout());
        listModel = new DefaultListModel<>();

        list = new JList<>(listModel);
        list.setFixedCellWidth(150);
        add(new JScrollPane(list), BorderLayout.CENTER);

        broadcastButton = new JButton("Broadcast");
        add(broadcastButton, BorderLayout.SOUTH);
    }
    public void addListSelectionListener(ListSelectionListener listener){
        list.addListSelectionListener(listener);
    }
    public void addBroadcastListener(ActionListener al){
        broadcastButton.addActionListener(al);
    }

    public void addConnection(ClientMessageConnection cmc){
        listModel.addElement(cmc);
    }

    public void removeConnection(ClientMessageConnection cmc){
        listModel.removeElement(cmc);
    }

    public JList<ClientMessageConnection> getList(){
        return list;
    }

}

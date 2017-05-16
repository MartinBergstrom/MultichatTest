package Server.GUI;


import Server.Clientconnections.ClientMessageConnection;
import Server.Clientconnections.Connections;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.sql.Connection;

/**
 * Created by Martin on 2017-05-14.
 */
public class ListOfConnections extends JPanel {
    private DefaultListModel<Connections> listModel;
    private JList<Connections> list;
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

    public void addConnection(Connections connections){
        listModel.addElement(connections);
    }

    public void removeConnection(Connections connections){
        listModel.removeElement(connections);
    }

    public JList<Connections> getList(){
        return list;
    }

}

package Server.HandleNewClients;

import Server.Clientconnections.ClientImageConnection;
import Server.Clientconnections.ClientMessageConnection;
import Server.Main.MainServer;
import Server.ServerGUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-10.
 *
 * Thread class which listens to new incoming client connections
 *
 */
public class HandleNewClientConnections implements Runnable{
    private ServerGUI gui;
    private Socket socket;

    private int port;

    private MainServer server;

    public HandleNewClientConnections(ServerGUI gui, int port, MainServer server){
        this.gui= gui;
        this.port = port;
        this.server=server;
    }

    private ClientImageConnection setUpImageConnection(){
        ClientImageConnection imageServer = null;
        try{
            ServerSocket imageServerSocket = new ServerSocket(4800);
            Socket imageSocket = imageServerSocket.accept();
            imageServer = new ClientImageConnection(imageSocket,gui);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageServer;
    }

    @Override
    public void run() {
        gui.updateMessageToTextArea("Waiting for clients to connect... ");
        try(ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

                ClientMessageConnection cct = null;
                cct = new ClientMessageConnection(socket, gui, setUpImageConnection());
                gui.updateMessageToTextArea("--- Client at IP address: " + server.getHostname() +
                        " just connected ----");
                gui.enableActive();
                server.addConnection(cct);
                gui.setNumberOfConnections(server.getNumberOfConnections());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

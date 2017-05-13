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
    private int port;
    private final int imagePort = 4800;

    private MainServer server;

    public HandleNewClientConnections(ServerGUI gui, int port, MainServer server){
        this.gui= gui;
        this.port = port;
        this.server=server;
    }


    @Override
    public void run() {
        gui.updateMessageToTextArea("Waiting for clients to connect... ");
        try(ServerSocket serverSocket = new ServerSocket(port);
            ServerSocket imageServerSocket = new ServerSocket(imagePort)){
            Socket socket;
            Socket imageSocket;
            while (true) {
                try {
                     socket = serverSocket.accept();
                    imageSocket = imageServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                ClientImageConnection cic = new ClientImageConnection(imageSocket,gui);

                ClientMessageConnection cmt = new ClientMessageConnection(socket, gui, cic);
                gui.updateMessageToTextArea("--- Client at IP address: " + server.getHostname() +
                        " just connected ----");
                gui.enableActive();
                server.addConnection(cmt);
                gui.setNumberOfConnections(server.getNumberOfConnections());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

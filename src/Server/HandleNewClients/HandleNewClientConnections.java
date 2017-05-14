package Server.HandleNewClients;

import Server.Clientconnections.ClientFileConnection;
import Server.Clientconnections.ClientImageConnection;
import Server.Clientconnections.ClientMessageConnection;
import Server.Main.MainServer;
import Server.GUI.ServerGUI;

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
    private final int filePort = 4900;

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
            ServerSocket imageServerSocket = new ServerSocket(imagePort);
            ServerSocket fileServerSocket = new ServerSocket(filePort)){

            Socket socket;
            Socket imageSocket;
            Socket fileSocket;
            while (true) {
                try {
                     socket = serverSocket.accept();
                     imageSocket = imageServerSocket.accept();
                     fileSocket = fileServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                ClientImageConnection cic = new ClientImageConnection(imageSocket,gui);
                ClientFileConnection cfc = new ClientFileConnection(fileSocket,gui);

                ClientMessageConnection cmt = new ClientMessageConnection(socket, gui, cic,cfc);
                gui.updateMessageToTextArea("--- Client at IP address: " + server.getHostname() +
                        " just connected ----");
                server.addConnection(cmt);
                gui.setNumberOfConnections(server.getNumberOfConnections());
            }
        }catch (IOException e){
            e.printStackTrace(); //Already shut down
        }
    }
}

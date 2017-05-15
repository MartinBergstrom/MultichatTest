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

    private MainServer server;

    public HandleNewClientConnections(ServerGUI gui, int port, MainServer server){
        this.gui= gui;
        this.port = port;
        this.server=server;
    }

    @Override
    public void run() {
        gui.updateMessageToTextArea("Waiting for clients to connect... ");
        try(ServerSocket serverSocket = new ServerSocket(port)){
            Socket socket;
            Socket imageSocket;
            Socket fileSocket;
            while (true) {
                try {
                    //accept 3 different connections, txt, image and file
                    socket = serverSocket.accept();
                    imageSocket = serverSocket.accept();
                    fileSocket = serverSocket.accept();
                    ClientImageConnection cic = new ClientImageConnection(imageSocket, gui);
                    ClientFileConnection cfc = new ClientFileConnection(fileSocket, gui);

                    ClientMessageConnection cmt = new ClientMessageConnection(socket, gui, cic, cfc);

                    gui.updateMessageToTextArea("--- Client at IP address: " + server.getHostname() +
                            " just connected ----");
                    server.addConnection(cmt);
                    gui.setNumberOfConnections(server.getNumberOfConnections());
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace(); //Already shut down
        }
    }
}

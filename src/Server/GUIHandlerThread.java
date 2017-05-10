package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-10.
 */
public class GUIHandlerThread implements Runnable{
    private ServerGUI gui;
    private Socket socket;
    private ServerSocket serverSocket;
    private MainServerHandler server;

    public GUIHandlerThread(ServerGUI gui, Socket socket, ServerSocket serverSocket, MainServerHandler server){
        this.gui= gui;
        this.socket=socket;
        this.serverSocket=serverSocket;
        this.server=server;
    }

    @Override
    public void run() {
        gui.updateMessageToTextArea("Waiting for clients to connect... ");
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            //check if old clients are disconnected, and in that case, remove them
            //this is a problem cus I if I write after the last client disconnected it crashes
            server.checkConnections();

            ClientConnection cct = null;
            cct = new ClientConnection(socket, gui);
            gui.updateMessageToTextArea("--- Client at IP address: " + server.getHostname() +
                    " just connected ----");
            gui.enableAbleToType();
            server.addConnection(cct);
        }
    }
}

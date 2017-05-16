package Server.HandleNewClients;

import Server.Clientconnections.ClientFileConnection;
import Server.Clientconnections.ClientImageConnection;
import Server.Clientconnections.ClientMessageConnection;
import Server.Clientconnections.Connections;
import Server.Main.MainServer;
import Server.GUI.ServerGUI;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Martin on 2017-05-10.
 *
 * Thread class which listens to new incoming clients.
 * To handle the case when multiple clients try to connect at the same time,
 * I map the clientName to a Connections object which will keep track on the three different connections from one client
 *
 */
public class HandleNewClientConnections implements Runnable{
    private ServerGUI gui;
    private int port;
    private Map<String,Connections> cMap;

    private MainServer server;

    public HandleNewClientConnections(ServerGUI gui, int port, MainServer server){
        this.gui= gui;
        this.port = port;
        this.server=server;
        cMap = new HashMap<>();
    }

    @Override
    public void run() {
        gui.updateMessageToTextArea("Waiting for clients to connect... ");
        try(ServerSocket serverSocket = new ServerSocket(port)){
            Socket socket;
            while (true) {
                try {
                    socket = serverSocket.accept();

                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    String connectionType = dis.readUTF();
                    String clientName = dis.readUTF();

                    if(!cMap.containsKey(clientName)){ //new client
                        cMap.put(clientName, new Connections(clientName));
                    }

                    switch (connectionType){
                        case "TextConnection":
                            ClientMessageConnection cmc = new ClientMessageConnection(socket,gui);
                            cMap.get(clientName).setCmc(cmc);
                            break;
                        case "ImageConnection":
                            ClientImageConnection cic = new ClientImageConnection(socket,gui);
                            cMap.get(clientName).setCic(cic);
                            break;
                        case "FileConnection":
                            ClientFileConnection cfc = new ClientFileConnection(socket,gui);
                            cMap.get(clientName).setCfc(cfc);
                            break;
                    }

                    if(cMap.get(clientName).allSet()){ //all the connections have been added from this particular client
                        server.addConnection(cMap.get(clientName));
                        gui.updateMessageToTextArea("--- Client at IP address: " + clientName +
                                " just connected ----");
                        gui.setNumberOfConnections(server.getNumberOfConnections());
                    }
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

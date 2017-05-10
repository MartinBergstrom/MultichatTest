package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-10.
 */
public class ConsoleHandlerThread implements Runnable{
    private Socket socket;
    private ServerSocket serverSocket;
    private MainServerHandler server;

    public ConsoleHandlerThread(Socket socket, ServerSocket serverSocket, MainServerHandler server){
        this.socket=socket;
        this.serverSocket=serverSocket;
        this.server=server;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Waiting for clients to connect...");
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            ClientConnection cct = null;
            cct = new ClientConnection(socket);
            server.newConsoleWriterIfEmpty();
            System.out.println("A new client at IP address: " + server.getHostname() +
                    " connected to the server");
            server.addConnection(cct);
        }
    }
}

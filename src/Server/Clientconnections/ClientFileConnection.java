package Server.Clientconnections;

import HandleDataTransfer.FileHandler;
import Server.GUI.ServerGUI;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-14.
 */
public class ClientFileConnection extends AbstractClientConnection {
    private File file;

    public ClientFileConnection(Socket sock, ServerGUI gui) {
        super(sock, gui);
        new Thread(new ClientReader()).start();
    }

    public void sendFile(File file){
        this.file = file;
        new Thread(new ClientWriter()).start();
    }

    class ClientWriter implements Runnable{
        @Override
        public void run() {
            FileHandler.sendFile(file,dos);
        }
    }

    class ClientReader implements Runnable{
        @Override
        public void run() {
            while (true){
                String header = null;
                try {
                    if(disconnected){
                        throw new IOException();
                    }
                    header = dis.readUTF();
                    if(header.equals("file")){
                        String fileName = dis.readUTF();
                        long fileSize = dis.readLong();
                        FileHandler.retreiveAndSaveFile(dis, fileName,fileSize);
                        gui.updateMessageToTextArea("--- Recevied a file named: " + fileName + " from client: " +
                                clientName + " and saved it in folder ReveivedFiles ---");
                    }
                } catch (IOException e) {
                    try{
                        socket.close();
                        return;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}

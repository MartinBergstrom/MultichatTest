package Client.ServerConnections;

import Client.GUI.ClientGUI;
import HandleDataTransfer.FileHandler;

import java.io.File;
import java.io.IOException;

/**
 * Created by Martin on 2017-05-13.
 */
public class FileHandlerClient extends AbstractConnectionToServer {
    private File file;

    public FileHandlerClient(String ipAddress, int port, ClientGUI gui) {
        super(ipAddress, port, gui);
        file = null;
    }

    @Override
    public void sendClientInfo(String clientName) {
        try {
            dos.writeUTF("FileConnection");
            dos.writeUTF(clientName);

            dis.readUTF(); //wait for ack
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new ServerReader()).start();
    }

    public void sendFile(File file){
        this.file = file;
        new Thread(new ServerWriter()).start();
    }

    class ServerWriter implements Runnable{
        @Override
        public void run() {
            //Put this logic in the calling class instead, use this to simply send it?
            FileHandler.sendFile(file,dos);
        }
    }

    class ServerReader implements Runnable{
        @Override
        public void run() {
            while (true) {
                try {
                    if (disconnected) {
                        throw new IOException(); //exit thread
                    }
                    String header = dis.readUTF();
                    if(header.equals("file")){
                        String filename = dis.readUTF();
                        System.out.println("filename is : " + filename);
                        long fileSize = dis.readLong();
                        FileHandler.retreiveAndSaveFile(dis,filename,fileSize);
                        gui.updateMessageToTextArea("--- Recevied a file named: " + filename +
                                " and saved it in folder ReveivedFiles ---");
                    }
                } catch (IOException e) {
                    try{
                        socket.close();
                    } catch (IOException e1) {}
                    return;
                }

            }
        }
    }
}

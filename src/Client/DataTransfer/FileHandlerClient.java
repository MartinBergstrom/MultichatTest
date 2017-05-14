package Client.DataTransfer;

import Client.ClientGUI;
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
            try {
                dos.writeUTF("file"); //set header
                dos.writeUTF(file.getName());
                dos.writeLong(file.length()); //write length of file in bytes,
                FileHandler.sendFile(file,dos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ServerReader implements Runnable{
        @Override
        public void run() {

        }
    }
}

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

    public void sendFile(File file) {
        FileHandler.askToAccept(file, dos); //check if server accepts to receive
        this.file = file;
    }

    class ServerWriter implements Runnable{
        @Override
        public void run() {
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
                    String message = dis.readUTF();
                    if(message.equals("accept")){ //server wants to send you file
                        String fileName = dis.readUTF();
                        long fileSize = dis.readLong();
                        String answer = gui.getConfirmDialog("--- Server wants to send you a file named: " + fileName +
                                " with the size: " + fileSize + " bytes. \n Do you accept?");
                        if(answer.equals("yes")) {
                            dos.writeUTF("sendMeFile"); //tell server to send it
                            dos.flush();
                            System.out.println("told the server yes");
                            FileHandler.retreiveAndSaveFile(dis, fileName, fileSize);
                            gui.updateMessageToTextArea("--- Recevied a file named: " + fileName +
                                    " and saved it in folder ReveivedFiles ---");
                        }else{
                            dos.writeUTF("No");
                        }
                    }else if(message.equals("sendMeFile")){ //server accepts and wants you to send it
                        new Thread(new ServerWriter()).start();
                        gui.updateMessageToTextArea("--- The file was successfully sent out to the server ---");
                    }else if(message.equals("No")){
                        gui.updateMessageToTextArea("--- Server declined to accept the file ---");
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

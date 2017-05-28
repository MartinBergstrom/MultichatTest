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

    public void sendFile(File file) {
        FileHandler.askToAccept(file, dos); //ask Client to accept this shit
        System.out.println("asked client to accept the mofo");
        this.file = file;
    }

    class ClientWriter implements Runnable{
        @Override
        public void run() {
            FileHandler.sendFile(file,dos);
        }
    }

    //Mainly used for reading incoming client messages and to call readFile or sendFile if accept
    class ClientReader implements Runnable{
        @Override
        public void run() {
            while (true){
                String header = null;
                try {
                    if(disconnected){
                        throw new IOException();
                    }
                    String message = dis.readUTF();
                    System.out.println("GOT MESSAGE FROM CLIENT AND THAT IS: " + message);
                    if(message.equals("accept")){ //client wants to send you shit
                        String fileName = dis.readUTF();
                        long fileSize = dis.readLong();
                        String answer = gui.getConfirmDialog("--- Client at: "+ clientName + " wants to send you a file named: " + fileName +
                                " with the size: " + fileSize + " bytes. \n Do you accept?");
                        if(answer.equals("yes")) {
                            dos.writeUTF("sendMeFile"); //tell client to send file
                            FileHandler.retreiveAndSaveFile(dis, fileName, fileSize); //start to receive the file
                            gui.updateMessageToTextArea("--- Recevied a file named: " + fileName +
                                    " and saved it in folder ReveivedFiles ---");

                        }else{
                            dos.writeUTF("No"); //tell client to not send file
                        }
                    }else if(message.equals("sendMeFile")){ //client wants you to send the file
                        new Thread(new ClientWriter()).start();
                        gui.updateMessageToTextArea("--- The file was successfully sent out to the client at: " + clientName + " ---");
                    }else if(message.equals("No")){
                        gui.updateMessageToTextArea("--- The client at : " + clientName + " declined to accept ---");
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

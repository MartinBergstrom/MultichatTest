package Server.Clientconnections;

import Server.GUI.ServerGUI;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-09.
 *
 * Base connection to client, will start ImageConnection
 */
public class ClientMessageConnection extends AbstractClientConnection {
    private ClientImageConnection imageServer;
    private ClientFileConnection fileServer;

    public ClientMessageConnection(Socket sock, ServerGUI gui, ClientImageConnection imageServer, ClientFileConnection fileServer){
        super(sock,gui);
        this.imageServer = imageServer;
        this.fileServer = fileServer;

        new Thread(new ClientReader()).start();
        gui.updateMessageToTextArea("Connection up and running... ");
    }


    public boolean sendMessage(String message){
        try{
            dos.writeUTF("msg");
            dos.writeUTF(message);
            dos.flush();
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return clientName;
    }

    public void sendPicture(BufferedImage image, String imageType){
         imageServer.sendImage(image,imageType);
    }

    public void sendFile(File file){
        fileServer.sendFile(file);
    }

    class ClientReader implements Runnable{
        @Override
        public void run(){
            if(!getDisconnect()){
                while (true){
                    try {
                        String header = dis.readUTF();
                        if(header.equals("msg")){
                            String message = dis.readUTF();
                            if(gui!=null){
                                gui.updateMessageToTextArea(message);
                            }else{
                                System.out.println(message);
                            }
                        }
                    } catch (IOException e) {
                        disconnect();
                        gui.updateMessageToTextArea("--- Client: " + clientName + " disconnected ---");
                        gui.checkConnections();
                        try {
                            socket.close();
                        } catch (IOException e1) {}
                        System.out.println("CLIENT DISCONNECTED, TERMINATE THREAD");
                        break;
                    }
                }
            }
        }
    }
}


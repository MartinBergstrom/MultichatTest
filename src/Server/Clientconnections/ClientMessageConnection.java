package Server.Clientconnections;

import Server.ServerGUI;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-09.
 *
 * Base connection to client, will start ImageConnection
 */
public class ClientMessageConnection extends AbstractClientConnection{
    private ClientImageConnection imageServer;

    public ClientMessageConnection(Socket sock, ServerGUI gui, ClientImageConnection imageServer){
        super(sock,gui);
        this.imageServer = imageServer;

        new Thread(new ClientReader()).start();
        gui.enableActive();
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

    public boolean sendPicture(BufferedImage image, String imageType){
        return imageServer.sendImage(image,imageType);
    }

    class ClientReader implements Runnable{
        @Override
        public void run(){
            if(!getDisconnect()){
                while (true){
                    try {
                        String header = dis.readUTF();
                        if(header.equals("file")){
                            String fileName = dis.readUTF();
                            int fileSize = dis.readInt();
                            gui.updateMessageToTextArea("Client wants to send a file named: " + fileName +
                                    " with the size of: " + fileSize + ", do you accept? (Y/N)");
                            /*if(gui.waitForUserConfirmation()){
                                send ACK to server
                                read that bitch with FileHandler and print if successfull
                            }*/
                        }
                        else if(header.equals("msg")){
                            String message = dis.readUTF();
                            if(gui!=null){
                                gui.updateMessageToTextArea(message);
                            }else{
                                System.out.println(message);
                            }
                        }
                    } catch (IOException e) {
                        disconnect();
                        gui.updateMessageToTextArea("--- Client: " + socket.getRemoteSocketAddress().toString() + " disconnected ---");
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


package Server.Clientconnections;

import Server.GUI.ServerGUI;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-09.
 *
 *
 */
public class ClientMessageConnection extends AbstractClientConnection {

    public ClientMessageConnection(Socket sock, ServerGUI gui){
        super(sock,gui);

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


package Server.Clientconnections;

import HandleDataTransfer.ImageHandler;
import Server.ServerGUI;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-13.
 */
public class ClientImageConnection extends AbstractClientConnection{

    public ClientImageConnection(Socket socket, ServerGUI gui) {
        super(socket,gui);
        new Thread(new ClientReader()).start();
        System.out.println("ClientImageConnection is up and running");
    }

    public boolean sendImage(BufferedImage img, String imgType){
        return ImageHandler.sendPicture(img,imgType,dos);
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
                    if(header.equals("pic")){
                        gui.updateMessageToTextArea("--- Received picture from client at: " +
                                socket.getRemoteSocketAddress().toString()+" ---");
                        BufferedImage img = ImageHandler.retrievePicture(dis);
                        if(img!=null){
                            gui.showImage(img);
                        }else {
                            System.out.println("Image was null :(");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
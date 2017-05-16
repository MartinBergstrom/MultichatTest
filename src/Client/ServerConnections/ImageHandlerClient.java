package Client.ServerConnections;

import Client.GUI.ClientGUI;
import HandleDataTransfer.ImageHandler;

import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Martin on 2017-05-13.
 *
 * Sets up a new socket connection for image transfer
 */
public class ImageHandlerClient extends AbstractConnectionToServer {
    private BufferedImage img;
    private String imgType;

    public ImageHandlerClient(String ipAddress, int port, ClientGUI gui) {
        super(ipAddress,port,gui); //this will set up the connection
    }

    @Override
    public void sendClientInfo(String clientName) {
        try {
            dos.writeUTF("ImageConnection");
            dos.writeUTF(clientName);

            dis.readUTF(); //wait for ack
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new ServerReader()).start();
    }

    public void sendImage(BufferedImage img, String imgType) {
        this.img = img;
        this.imgType = imgType;
        new Thread(new ServerWriter()).start();
    }

    class ServerWriter implements Runnable{
        @Override
        public void run() {
            ImageHandler.sendPicture(img, imgType, dos);
        }
    }

    class ServerReader implements Runnable{
        @Override
        public void run() {
            while (true){
                try {
                    if(disconnected){
                        throw  new IOException(); //exit thread
                    }
                    String header = dis.readUTF();
                    if(header.equals("pic")) {
                        gui.updateMessageToTextArea("--- Picture from server recieved: ---");
                        BufferedImage img = ImageHandler.retrievePicture(dis);
                        if (img != null) {
                            gui.showImage(img);
                        } else {
                            System.out.println("Image was null :(");
                        }
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
package Client.DataTransfer;

import Client.ClientGUI;
import HandleDataTransfer.ImageHandler;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Martin on 2017-05-13.
 *
 * Sets up a new socket connection for image transfer
 */
public class ImageHandlerClient {
    private String ipAddress;
    private int port;
    private Socket socket;

    private BufferedImage img;
    private String imgType;

    private DataInputStream dis;
    private DataOutputStream dos;

    private ClientGUI gui;
    private boolean disconnected = false;

    public ImageHandlerClient(String ipAddress, int port, ClientGUI gui) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.gui = gui;
        setUpConnection();
    }

    private void setUpConnection(){
        try{
            socket = new Socket(ipAddress,port);
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);

            new Thread(new ServerReader()).start();
            System.out.println("ImageHandlerClient is now connected and waiting for images");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void disconnect(){
        disconnected = true;
    }
}
package Server.Clientconnections;

import HandleDataTransfer.ImageHandler;
import Server.ServerGUI;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Created by Martin on 2017-05-13.
 */
public class ClientImageConnection {
    private Socket clientSocket;
    private boolean disconnected = false;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ServerGUI gui;

    public ClientImageConnection(Socket socket, ServerGUI gui) {
        this.clientSocket = socket;
        this.gui = gui;
        setUpConnection();
    }

    private void setUpConnection(){
        try {
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);

            new Thread(new ClientReader()).start();
            System.out.println("ClientImageConnection is up and running");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                                clientSocket.getRemoteSocketAddress().toString()+" ---");
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
                        clientSocket.close();
                        return;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public void disconnect(){
        disconnected = true;
    }
}
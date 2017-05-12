package Server;


import HandleDataTransfer.PictureHandler;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Created by Martin on 2017-05-09.
 */
public class ClientConnection {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private boolean disconnect = false;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ServerGUI gui;

    public ClientConnection(Socket sock){
        this.socket = sock;
        setUpConnection();
        System.out.println("Connection up and running...");
    }

    public ClientConnection(Socket sock, ServerGUI gui){
        this.socket=sock;
        this.gui = gui;

        setUpConnection();
        gui.enableActive();
        gui.updateMessageToTextArea("Connection up and running... ");
    }

    private void setUpConnection(){
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);

            new Thread(new ClientReader()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean sendMessage(String message){
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
        return PictureHandler.sendPicture(image,imageType,dos);
    }

    class ClientReader implements Runnable{
        @Override
        public void run(){
            if(!disconnect){
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
                        try{
                            disconnect = true;
                            socket.close();
                            is.close();
                            os.close();
                            gui.updateMessageToTextArea("--- Client: " + socket.getRemoteSocketAddress().toString() + " disconnected ---");
                            gui.checkConnections();
                            System.out.println("CLIENT DISCONNECTED, TERMINATE THREAD");
                            break;
                        }catch (IOException e2) {}
                    }
                }
            }
        }
    }

    public boolean isDisconnected(){
        return disconnect;
    }
}


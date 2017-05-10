package Server;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * Created by Martin on 2017-05-09.
 */
public class ClientConnection {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private boolean disconnect = false;
    private BufferedReader reader;
    private BufferedWriter writer;

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
        gui.enableAbleToType();
        gui.updateMessageToTextArea("Connection up and running... ");
    }

    private void setUpConnection(){
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
            new Thread(new ClientReader()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean sendMessage(String message){
        try{
            writer.write(message);
            writer.newLine();
            writer.flush();
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    class ClientReader implements Runnable{
        @Override
        public void run(){
            if(!disconnect){
                reader = new BufferedReader(new InputStreamReader(is));
                writer = new BufferedWriter(new OutputStreamWriter(os));
                try{
                    writer.write("SERVER -  Hello Client");
                    writer.newLine();
                    writer.flush();
                }catch(IOException e){
                    e.printStackTrace();
                }
                while (true){
                    try {
                        String message = reader.readLine();
                        if(gui!=null){
                            gui.updateMessageToTextArea(message);
                        }else{
                            System.out.println(message);
                        }

                    } catch (IOException e) {
                        try{
                            socket.close();
                            is.close();
                            os.close();
                            gui.updateMessageToTextArea("--- Client: " + socket.getRemoteSocketAddress().toString() + " disconnected ---");
                            System.out.println("CLIENT DISCONNECTED, TERMINATE THREAD");
                            break;
                        }catch (IOException e2) {}
                    }
                }
            }
            disconnect = true;
        }
    }

    public boolean isDisconnected(){
        return disconnect;
    }
}


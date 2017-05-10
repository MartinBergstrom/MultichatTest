package Server;


import java.io.*;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Created by Martin on 2017-05-09.
 */
public class ClientConnectionThread implements Runnable {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private boolean disconnect = false;
    private BufferedReader reader;
    private BufferedWriter writer;

    private ServerGUI gui;

    public ClientConnectionThread(Socket sock, ServerGUI gui){
        this.socket=sock;
        this.gui = gui;
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connection up and running...");
        if(gui!=null){
            gui.enableAbleToType();
        }
    }

    public boolean sendMessage(String message){
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

    @Override
    public void run() {
        if(!disconnect){
            reader = new BufferedReader(new InputStreamReader(is));
            writer = new BufferedWriter(new OutputStreamWriter(os));
            try{
                writer.write("SERVER -  Hello Client");
                writer.newLine();
                writer.flush();
            }catch(IOException e3){
                e3.printStackTrace();
            }
            while (true){
                try {
                    String message = reader.readLine();
                    if(message.equals("CLIENT - END")) {
                        break;
                    }
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
                        System.out.println("CLIENT DISCONNECTED, TERMINATE THREAD");
                        break;
                    }catch (IOException e2) {}
                }
            }
        }
        disconnect = true;
    }

    public boolean isDisconnected(){
        return disconnect;
    }
}


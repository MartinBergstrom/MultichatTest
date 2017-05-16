package Client.ServerConnections;

import Client.GUI.ClientGUI;

import java.io.IOException;

/**
 * Created by Martin on 2017-05-16.
 */
public class MessageHandlerClient extends AbstractConnectionToServer {


    public MessageHandlerClient(String ipAddress, int port, ClientGUI gui) {
        super(ipAddress, port, gui);
    }

    @Override
    public void sendClientInfo(String ClientName) {
        try {
            dos.writeUTF("TextConnection");
            dos.writeUTF(clientName);

            dis.readUTF(); //wait for ack
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new ServerReader()).start();
    }

    public boolean sendMessage(String message){
        try {
            dos.writeUTF("msg");
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            if(gui!=null){
                gui.updateMessageToTextArea("Something went wrong, couldn't send the message");
            }else{
                System.out.println("Something went wrong, couldn't send the message");
            }
            return false;
        }
        return true;
    }


    class ServerReader implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String header = dis.readUTF();
                    if(header.equals("msg")){
                        String message = dis.readUTF();
                        gui.updateMessageToTextArea(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    gui.updateMessageToTextArea("Lost connection to server, shutting down... ");
                    try {
                        Thread.sleep(2500);
                        socket.close(); //this also closes is and os
                        System.exit(0);
                    } catch (IOException e1) {}
                    catch (InterruptedException e1) {}
                }
            }
        }
    }
}

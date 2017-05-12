package Server;

import GuiTemplate.GUITemplate1;

/**
 * Created by Martin on 2017-05-09.
 */
public class ServerGUI extends GUITemplate1 {
    private MainServerHandler server;

    public ServerGUI(){
        super("Server");
    }
    public void addServer(MainServerHandler server){
        this.server = server;
    }

    @Override
    public void sendMessage(String message) {
        message = super.modifyMessage(message, "SERVER", server.getHostname());
        if(server.broadCastMessage(message)){
            updateMessageToTextArea(message);
        }
    }

    @Override
    public boolean sendPicture() {
        if(server.broadcastImage(img, imgType)){
            updateMessageToTextArea("\n--- Picture was successfully broadcasted to the clients ---");
            return true;
        }
        updateMessageToTextArea("\n--- Could not broadcast the image, error occured ---");
        return false;
    }

    //smells bad
    public void checkConnections(){
        server.checkConnections();
        if(server.isConnectionsEmpty()){
            disableActive();
        }else{
            enableActive();
        }
    }
}
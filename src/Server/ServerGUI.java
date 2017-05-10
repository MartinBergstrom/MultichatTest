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
    public boolean sendMessage(String message) {
        message = server.modidfyMessage(message);
        if(server.broadCastMessage(message)){
            updateMessageToTextArea(message);
            return true;
        }
        return false;
    }

}

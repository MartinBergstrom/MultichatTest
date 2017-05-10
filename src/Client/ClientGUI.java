package Client;

import GuiTemplate.GUITemplate1;

/**
 * Created by Martin on 2017-05-08.
 */

public class ClientGUI extends GUITemplate1 {
    private Client client;

    public ClientGUI(){
        super("Client");
    }

    public void addClient(Client c){
        this.client = c;
    }

    @Override
    public boolean sendMessage(String message) {
        if(ableToType) {
            message = Client.modifyMessage(message);
            if (client.sendMessage(message)) {
                updateMessageToTextArea(message);
                return true;
            }
        }
        return false;
    }

}
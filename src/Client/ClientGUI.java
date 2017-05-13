package Client;

import GuiTemplate.GUITemplate1;

/**
 * Created by Martin on 2017-05-08.
 *
 *
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
    public void sendMessage(String message) {
        if(ableToType) {
            message = super.modifyMessage(message, "CLIENT", client.getHostName());
            if (client.sendMessage(message)) {
                updateMessageToTextArea(message);
            }
        }
    }

    @Override
    public boolean sendPicture() {
        if(client.sendPicture(img,imgType)){
            updateMessageToTextArea("\n--- Picture was successfully sent to the server ---");
            return true;
        }
        updateMessageToTextArea("\n--- Could not send the image, error occured ---");
        return false;
    }
}
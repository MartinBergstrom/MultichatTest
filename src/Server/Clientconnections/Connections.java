package Server.Clientconnections;

/**
 * Created by Martin on 2017-05-16.
 */
public class Connections {
    private ClientMessageConnection cmc;
    private ClientImageConnection cic;
    private ClientFileConnection cfc;
    private int count;

    private String clientName;

    public Connections(String clientName){
        this.clientName = clientName;
        count = 0;
    }
    public boolean allSet(){ //If all 3 connections are up and running to this particular client
        return count == 3;
    }
    public void setCmc(ClientMessageConnection cmc) {
            this.cmc = cmc;
            count++;
    }

    public void setCic(ClientImageConnection cic) {
            this.cic = cic;
            count++;
    }

    public void setCfc(ClientFileConnection cfc) {
            this.cfc = cfc;
            count++;
    }

    public ClientMessageConnection getCmc(){
        return cmc;
    }

    public ClientImageConnection getCic() {
        return cic;
    }

    public ClientFileConnection getCfc() {
        return cfc;
    }

    public boolean getDisconnect(){
        if(cmc.getDisconnect() || cic.getDisconnect() || cfc.getDisconnect()){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public String toString() {
        return clientName;
    }
}

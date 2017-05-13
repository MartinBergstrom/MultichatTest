package HandleDataTransfer;

import java.io.*;

/**
 * Created by Martin on 2017-05-12.
 */
public class FileHandler {
    public static boolean sendFile(File file, DataOutputStream dos){
        try {
            //Put this logic in the calling class instead, use this to simply send it?
            dos.writeUTF("file"); //set header
            dos.writeUTF(file.getName());
            dos.writeLong(file.length()); //write length of file in bytes,
            ///
            

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File retreiveFile(DataInputStream dis){
        return null;
    }

}

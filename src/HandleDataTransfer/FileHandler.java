package HandleDataTransfer;

import java.io.*;

/**
 * Created by Martin on 2017-05-12.
 */
public class FileHandler {
    public static boolean sendFile(File file, DataOutputStream dos){
        try {
            ///
            FileInputStream fileInput = new FileInputStream(file);
            byte[] buffer = new byte[8192];

            int count = 0;
            while( (count = fileInput.read(buffer)) >=0){ //read into the buffer
                dos.write(buffer,0,count); //write from buffer
            }
            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File retreiveFile(DataInputStream dis){
        return null;
    }

}

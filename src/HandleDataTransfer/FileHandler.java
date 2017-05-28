package HandleDataTransfer;

import Client.ServerConnections.FileHandlerClient;

import java.io.*;

/**
 * Created by Martin on 2017-05-12.
 */
public class FileHandler {
    public static boolean sendFile(File file, DataOutputStream dos){
        try( FileInputStream fileInput = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];

            int count ;
            while( (count = fileInput.read(buffer)) > 0){ //read into the buffer
                dos.write(buffer,0,count); //write from buffer
            }
            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void retreiveAndSaveFile(DataInputStream dis, String filename, long fileSize){
        final String newDirPath = System.getProperty("user.dir") + File.separator + "ReceivedFiles";
        File newDir = new File(newDirPath);
        System.out.println(newDirPath);
        if(newDir.mkdirs()){
            System.out.println("CREATED DIR");
        }
        final String newFilePath = newDirPath + File.separator + filename;
        System.out.println(newFilePath);
        File destinationfile = new File(newFilePath);
        System.out.println("filesize is: " + fileSize);
        try(FileOutputStream fos = new FileOutputStream(destinationfile)) {
            byte[] buffer = new byte[8192];
            int count;
            while(destinationfile.length() != fileSize && (count = dis.read(buffer)) > 0){ //read the incoming file
                fos.write(buffer,0,count); //write to the output file
            }
            System.out.println("WROTE THE FILE XD");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void askToAccept(File file, DataOutputStream dos) {
        try {
            dos.writeUTF("accept");
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

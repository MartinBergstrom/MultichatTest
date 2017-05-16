package HandleDataTransfer;

import java.io.*;

/**
 * Created by Martin on 2017-05-12.
 */
public class FileHandler {
    public static boolean sendFile(File file, DataOutputStream dos){
        try( FileInputStream fileInput = new FileInputStream(file)) {
            dos.writeUTF("file"); //set header
            dos.writeUTF(file.getName());
            dos.writeLong(file.length()); //write length of file in bytes,
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
        final String newFilePath = System.getProperty("user.dir") + File.separator + "ReceivedFiles" + File.separator + filename;
        System.out.println(newFilePath);
        File destinationfile = new File(newFilePath);
        try(FileOutputStream fos = new FileOutputStream(destinationfile)) {
            byte[] buffer = new byte[8192];
            int count;
            while(destinationfile.length() != fileSize && (count = dis.read(buffer)) > 0){ //read the incoming file
                fos.write(buffer,0,count); //write to the output file
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

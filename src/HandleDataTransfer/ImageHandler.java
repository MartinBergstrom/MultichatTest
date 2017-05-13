package HandleDataTransfer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Martin on 2017-05-11.
 *
 * Sets up a temporary connection to send and retreieve pictures
 */
public class ImageHandler {
    /**
     * Static method to send picture, be sure to have the right ImageType, "jpg", "png" etc..
     *
     * @param img the image to send
     * @param imageType the type of image, ex "jpg", "png" etc.
     * @param dos the DataOutputStream
     * @return true if the picture sent successfully, false otherwise
     */
    public static boolean sendPicture(BufferedImage img, String imageType, DataOutputStream dos) {
        boolean success = false;
        try {
            dos.writeUTF("pic");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img,imageType,baos);
            baos.flush();
            byte[] imageArray = baos.toByteArray();
            baos.close();

            //write picture to dataoutputstream
            dos.writeInt(imageArray.length);
            dos.write(imageArray);

            success = true;
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * Retrieves a picture from the DataInputStream, assuming header flag "pic" is already read
     *
     * @param dis the input stream to get the image from
     * @return the BufferedImage, null if error occured
     */
    public static BufferedImage retrievePicture(DataInputStream dis) {
        BufferedImage img = null;
        try {
            int imageLength = dis.readInt();
            byte[] imageArray = new byte[imageLength];
            for(int i = 0; i<imageLength; i++){
                imageArray[i] = dis.readByte();
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(imageArray);
            img = ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }
}

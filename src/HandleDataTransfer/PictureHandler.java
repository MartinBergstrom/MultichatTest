package HandleDataTransfer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Martin on 2017-05-11.
 *
 * Thread class to send and retrieve pictures over the connection
 */
public class PictureHandler {
    private InputStream is;
    private OutputStream os;

    public PictureHandler(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }

    /**
     *
     * @param img the image to send
     * @param imageformat "jpg", "png" etc..
     * @return true if picture was successfully sent
     */
    public synchronized boolean sendPicture(Image img, String imageformat) {
        boolean success = false;
        try {
            imageformat = imageformat.toLowerCase();
            if(ImageIO.write((BufferedImage)img, imageformat, os)){
                success = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }


    public synchronized Image retrievePicture() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }
}

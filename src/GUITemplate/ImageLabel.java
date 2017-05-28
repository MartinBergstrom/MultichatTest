package GUITemplate;

import javax.swing.*;

/**
 * Created by Martin on 2017-05-28.
 */
public class ImageLabel extends JLabel {
    private final String emojiCode;

    public ImageLabel(ImageIcon imgIcon, String emojiCode) {
        super(imgIcon);
        this.emojiCode = emojiCode;
    }

    @Override
    public String toString() {
        return emojiCode;
    }
}

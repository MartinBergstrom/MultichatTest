package GUITemplate;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by Martin on 2017-05-21.
 */
public class EmojiPanel extends JPanel implements MouseListener {
    private JTabbedPane tabbedPane;
    private final String path = System.getProperty("user.dir") + File.separator + "emojis";
    private File emojiFolder = new File(path);

    //allowed extensions
    private final String[] EXTENSIONS = new String[]{"gif", "png", "bmp"};
    private FilenameFilter image_filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            for(String ext: EXTENSIONS){
                if(name.endsWith("." + ext)){
                    return true;
                }
            }
            return false;
        }
    };

    private JPanel mainPanel;
    private GridLayout gridLayout;

    private JTextField textField;

    public EmojiPanel(JTextField textField){
        this.textField = textField;
        setBounds(100,62,350,200);
        gridLayout = new GridLayout(250,10,2,2); //2427  emojis
        mainPanel = new JPanel(gridLayout);
        //mainPanel.setPreferredSize(new Dimension(300,150));
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getViewport().setPreferredSize(new Dimension(320,170));

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Emojis page 1",null, scrollPane, "smiley");
        add(tabbedPane);

        showAllEmojis();
//        mainPanel.revalidate();
//        mainPanel.repaint();
        setOpaque(false);
        setVisible(true);
    }

    private void showAllEmojis(){
        if(emojiFolder.isDirectory()){
            for(File f: emojiFolder.listFiles(image_filter)){
                BufferedImage img = null;

                try{
                    img = ImageIO.read(f);
                    Image resizedImg = img.getScaledInstance(25,25,Image.SCALE_DEFAULT);
                    ImageIcon icon = new ImageIcon(resizedImg);

                    ImageLabel label = new ImageLabel(icon,f.getName());
                    label.addMouseListener(this);
                    mainPanel.add(label);

//                    System.out.println("image: " + f.getName());
//                    System.out.println(" width : " + img.getWidth());
//                    System.out.println(" height: " + img.getHeight());
//                    System.out.println(" size  : " + f.length());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        textField.setText(textField.getText() + "[" + e.getComponent().toString()+"]");
        this.setVisible(false);
        textField.requestFocusInWindow();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
package GuiTemplate;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Martin on 2017-05-09.
 *
 * Abstract superclass for the client and server gui's
 *
 * Subclsses will need to implement specific sendMessage and sendPicture
 *
 */
public abstract class GUITemplate1 extends JFrame {
    private JTextPane textPane;
    private JTextField textField;
    private JButton sendButton;
    private JMenu sendData;
    private JMenuItem pictureMenuItem;
    private JMenuItem fileMenuItem;
    protected JPanel messagePanel;

    protected boolean ableToType;

    public GUITemplate1(String name) {
        super(name);
        ableToType = false;
        setSize(new Dimension(440, 300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        sendData = new JMenu("Send Data");
        pictureMenuItem = new JMenuItem("Image");
        sendData.add(pictureMenuItem);
        fileMenuItem = new JMenuItem("File");
        sendData.add(fileMenuItem);
        menuBar.add(sendData);
        setJMenuBar(menuBar);

        //create and add TextPane
        DefaultStyledDocument document = new DefaultStyledDocument();
        textPane = new JTextPane(document);
        textPane.setEditable(false);

        add(new JScrollPane(textPane));

        //create and add MessagePanel with text input and button
        messagePanel = new JPanel(new FlowLayout());
        add(messagePanel, BorderLayout.SOUTH);

        textField = new JTextField("");
        textField.setPreferredSize(new Dimension(200, 20));

        sendButton = new JButton("Send");

        messagePanel.add(textField);
        messagePanel.add(sendButton);

        addListeners();
        setVisible(true);
    }

    public void addListeners() {
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (!text.equals("")) {
                    sendMessage(text);
                    textField.setText("");
                }
            }
        });
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = textField.getText();
                    if (!text.equals("")) {
                        sendMessage(text);
                        textField.setText("");
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        pictureMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) { //user selected a file
                    File choosenFile = fileChooser.getSelectedFile();
                    BufferedImage img = null;
                    try {
                        String rawImageType = "";
                        ImageInputStream input = ImageIO.createImageInputStream(choosenFile);
                        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(input);
                        while (imageReaders.hasNext()) {
                            ImageReader reader = (ImageReader) imageReaders.next();
                            rawImageType = reader.getFormatName();
                        }
                        if (rawImageType.equalsIgnoreCase("JPEG")) {
                            rawImageType = "jpg";
                        }
                        String imgType = rawImageType;
                        System.out.println("imgTYPE IS :" + imgType);
                        if ((img = ImageIO.read(input)) == null) {
                            updateMessageToTextArea("Could not read that image");
                        }
                        sendPicture(img, imgType);
                        showSentImage(img);

                    } catch (IOException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Selected file is not image file");
                        return;
                    }
                }
            }
        });
        fileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {//user selected a file
                    sendFile(fileChooser.getSelectedFile());
                }
            }
        });
    }

    public synchronized void updateMessageToTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StyledDocument doc = textPane.getStyledDocument();
                StyleContext context = new StyleContext();
                Style style = context.addStyle("test", null);
                StyleConstants.setForeground(style, Color.BLACK);
                try {
                    doc.insertString(doc.getLength(), text + "\n", style);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                textPane.setCaretPosition(doc.getLength());
            }
        });
    }

    private void showSentImage(BufferedImage img) {
        Image resizedImage = img.getScaledInstance(100, -1, Image.SCALE_SMOOTH); //negative height means keep aspect ratio
        ImageIcon picture = new ImageIcon(resizedImage);
        textPane.insertIcon(picture);
    }


    public void showImage(BufferedImage img){
        SwingUtilities.invokeLater(new Runnable() { //schedule task when called from something else rather than GUI thread itsefl?
            @Override
            public void run() {
                Image resizedImage = img.getScaledInstance(200,-1,Image.SCALE_SMOOTH); //negative height means keep aspect ratio
                ImageIcon picture = new ImageIcon(resizedImage);
                textPane.insertIcon(picture);
                updateMessageToTextArea("");
            }
        });
    }

    public void enableActive(){
        ableToType = true;
        textField.setEditable(true);
        sendButton.setEnabled(true);
        sendData.setEnabled(true);
    }

    public void disableActive(){
        ableToType = false;
        textField.setEditable(false);
        sendButton.setEnabled(false);
        sendData.setEnabled(false);
    }

    /**
     * Modifies the string to cut it each 50 characters for readability,
     * also adds the type(SERVER/CLIENT) and hostname tag before the actual message
     *
     * @param message
     * @param typetag
     * @param hostname
     * @return
     */
    public static String modifyMessage(String message, String typetag, String hostname){
        if(message.length()>50) {
            StringBuilder sb = new StringBuilder();
            String firstString = message.substring(0,51);
            sb.append(firstString);
            sb.append("\n");
            int index = 51;
            while((firstString = message.substring(index)).length() >50) {
                sb.append(firstString.substring(0,index));
                sb.append("\n");
                index += 50;
            }
            sb.append(message.substring(index));
            return typetag+ ": " + hostname + " - " + sb.toString();
        }
        return typetag+ ": " + hostname + " - " + message;
    }


    public abstract void sendMessage(String message);
    public abstract void sendPicture(BufferedImage img, String imgType);
    public abstract void sendFile(File file);

}


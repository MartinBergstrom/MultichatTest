package GUITemplate;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Martin on 2017-05-09.
 *
 * Abstract superclass for the client and server gui's
 *
 * Subclasses will need to implement specific sendMessage and sendPicture
 *
 */
public abstract class GUITemplate1 extends JFrame {
    protected JPanel backgroundPanel;
    //add LyaeredPane with all this in first and emojis pop up in second pane
    private JTextPane textPane;
    private JButton emojiButton;
    private JTextField textField;
    private JButton sendButton;
    private JMenu sendData;
    private JMenuItem pictureMenuItem;
    private JMenuItem fileMenuItem;

    protected JPanel mainPanel;
    protected JPanel messagePanel;
    protected boolean ableToType;

    protected JLayeredPane layeredPane;
    private EmojiPanel emojiPanel;

    public GUITemplate1(String name) {
        super(name);
        backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setSize(new Dimension(550,380));
        ableToType = false;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        layeredPane = new JLayeredPane();

        JMenuBar menuBar = new JMenuBar();
        sendData = new JMenu("Send Data");
        pictureMenuItem = new JMenuItem("Select Image.. ");
        sendData.add(pictureMenuItem);
        fileMenuItem = new JMenuItem("Select File.. ");
        sendData.add(fileMenuItem);
        menuBar.add(sendData);
        setJMenuBar(menuBar);


        //----------- create main panel -----------------
        mainPanel = new JPanel();
        mainPanel.setBounds(0,0,500,300);
        mainPanel.setLayout(new BorderLayout());

        //mainPanel.setPreferredSize(new Dimension(500, 300));

        //create and add TextPane
        DefaultStyledDocument document = new DefaultStyledDocument();
        textPane = new JTextPane(document);
        textPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textPane);
        //scrollPane.setSize(new Dimension(200,100));
        mainPanel.add(scrollPane,BorderLayout.CENTER);

        //create and add MessagePanel with text input and button
        messagePanel = new JPanel(new FlowLayout());
        mainPanel.add(messagePanel, BorderLayout.SOUTH);

        emojiButton = new JButton("Show Emojis");

        textField = new JTextField("");
        textField.setPreferredSize(new Dimension(200, 20));

        sendButton = new JButton("Send");

        messagePanel.add(emojiButton);
        messagePanel.add(textField);
        messagePanel.add(sendButton);
        //----------------- end of main panel ----------------------
        backgroundPanel.setOpaque(false);
        //layeredPane.setBounds(0,0,500,350);
        layeredPane.setBackground(Color.BLACK);
        layeredPane.add(mainPanel,JLayeredPane.DEFAULT_LAYER);
        emojiPanel = new EmojiPanel(textField);
        layeredPane.add(emojiPanel, JLayeredPane.POPUP_LAYER);
        emojiPanel.setVisible(false);

        layeredPane.setVisible(true);

        backgroundPanel.add(layeredPane, BorderLayout.CENTER);
        setContentPane(backgroundPanel);

        addListeners();
        setVisible(true);
    }

    private String getCurrentTime(){
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public void addListeners() {
        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(emojiPanel.isVisible()){
                    emojiPanel.setVisible(false);
                }else{
                    emojiPanel.setVisible(true);
                }
            }
        });
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

    public synchronized void insertNewLine(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StyledDocument doc = textPane.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(),"\n", null);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                textPane.setCaretPosition(doc.getLength());
            }
        });
    }

    public synchronized void updateMessageToTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StyledDocument doc = textPane.getStyledDocument();
                StyleContext context = new StyleContext();

                //main style
                Style style = context.addStyle("test", null);
                StyleConstants.setForeground(style, Color.BLACK);
                StyleConstants.setFontSize(style,12);

                MutableAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setLineSpacing(set,0.18f);
                textPane.setParagraphAttributes(set,false);

                //time style
                Style timeStyle = context.addStyle("time", null);
                StyleConstants.setFontSize(timeStyle,9);
                try {
                    doc.insertString(doc.getLength(),getCurrentTime() + " ",timeStyle);
                    displayTextAndCheckForEmojis(text,doc,style);
                    //doc.insertString(doc.getLength(), text + "\n", style);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                textPane.setCaretPosition(doc.getLength());

                if(!isFocused()){ //if the focus isn't already on the window
                    toFront(); //make the app "blink/notify" in the OS task bar
                }
            }
        });
    }

    /**
     * Search the string for any emojis in unicode, will be in the form [1f1e6-1f1eb.png]
     *
     * @param text the string to check
     * @param doc
     * @param style
     */
    private void displayTextAndCheckForEmojis(String text, StyledDocument doc, Style style) throws BadLocationException {
        String emojiCode = "";
        String line = "";
        for(int i = 0; i<=text.length(); i++){
            if(i == text.length()){
                doc.insertString(doc.getLength(),line,style);
                doc.insertString(doc.getLength(), "\n",null);
                return;
            }
            if(text.charAt(i) == '['){
                System.out.println("DETECTED NEW [");
                doc.insertString(doc.getLength(),line,style);
                line += text.charAt(i);
                i++;
                while (text.charAt(i) != ']') {
                    emojiCode += text.charAt(i);
                    line += text.charAt(i);
                    i++;
                    if(i == text.length()){
                        break;
                    }
                }
                try{
                    showEmoji(emojiCode);
                    emojiCode = "";
                } catch (IOException e) {
                    e.printStackTrace();
                    doc.insertString(doc.getLength(),line,style); //add the line if it wasn't a valid emojicode
                }
                //the emoji was added so remove the temporary line read
                line = "";
            }else{
                line += text.charAt(i);
            }
        }
      }

    //show in text
    private void showEmoji(String code) throws IOException {
        final String path = System.getProperty("user.dir") + File.separator + "emojis" + File.separator + code;
        BufferedImage img = ImageIO.read(new File(path));

        Image resizedImg = img.getScaledInstance(16,16,Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(resizedImg);
        textPane.insertIcon(icon);
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
                insertNewLine();
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

    public String getConfirmDialog(String message){
        String answ = "";
        int dialogButtons = JOptionPane.YES_NO_OPTION;
        int result = JOptionPane.showConfirmDialog (this, message,"Warning",dialogButtons);
        if(result == JOptionPane.YES_OPTION){
            answ = "yes";
        }else {
            answ = "no";
        }
        return answ;
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
//        if(message.length()>50) {
//            StringBuilder sb = new StringBuilder();
//            String firstString = message.substring(0,51);
//            sb.append(firstString);
//            sb.append("\n");
//            int index = 51;
//            while((firstString = message.substring(index)).length() >50) {
//                sb.append(firstString.substring(0,index));
//                sb.append("\n");
//                index += 50;
//            }
//            sb.append(message.substring(index));
//            return typetag+ ": " + hostname + " - " + sb.toString();
//        }
        return typetag+ ": " + hostname + " - " + message;
    }


    public abstract void sendMessage(String message);
    public abstract void sendPicture(BufferedImage img, String imgType);
    public abstract void sendFile(File file);

}


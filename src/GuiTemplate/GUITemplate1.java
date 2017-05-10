package GuiTemplate;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Martin on 2017-05-09.
 *
 * Abstract superclass for the client and server gui's
 *
 * Subclsses will need to implement specific sendMessage
 *
 */
public abstract class GUITemplate1 extends JFrame{
    protected JTextArea textArea;
    protected JTextField textField;
    protected JButton sendButton;
    protected boolean ableToType;

    public GUITemplate1(String name){
        super(name);
        ableToType = false;
        setSize(new Dimension(400,300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        //textArea.setPreferredSize(new Dimension(200,220));
        add(new JScrollPane(textArea));

        JPanel messagePanel = new JPanel(new FlowLayout());
        add(messagePanel,BorderLayout.SOUTH);
        textField = new JTextField("Write message here");
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(200,20));
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        messagePanel.add(textField);
        messagePanel.add(sendButton);
        addListeners();
        setVisible(true);
    }
    public void addListeners(){
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if(!text.equals("")){
                    sendMessage(text);
                    textField.setText("");
                }
            }
        });
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() ==  KeyEvent.VK_ENTER){
                    String text = textField.getText();
                    if(!text.equals("")){
                        sendMessage(text);
                        textField.setText("");
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    public synchronized void updateMessageToTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append("\n" + text);
            }
        });
    }

    public void enableAbleToType(){
        ableToType = true;
        textField.setEditable(true);
        sendButton.setEnabled(true);
    }

    public void disableAbleToType(){
        ableToType = false;
        textField.setEditable(false);
        sendButton.setEnabled(false);
    }

    public abstract boolean sendMessage(String message);

}


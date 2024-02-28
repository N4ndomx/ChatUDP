package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PrivateChatUI {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private String name;
    private UDPClient client;

    public PrivateChatUI(UDPClient client, String name) {
        this.client = client;
        this.name = name;
        createUI();
    }

    private void createUI() {
        frame = new JFrame("Private Client  " + name);
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        messageField = new JTextField(25);
        sendButton = new JButton("Send");

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(chatArea));
        panel.add(messageField);
        panel.add(sendButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        sendButton.addActionListener((ActionListener) new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                messageField.setText("");
            }
        });

    }

    public void displayMessage(String message) {
        chatArea.append(message + "\n");
    }
}
package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClientUI {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton privateButton;

    private UDPClient client;

    public ChatClientUI(UDPClient client) {
        this.client = client;
        createUI();
    }

    private void createUI() {
        frame = new JFrame("Chat Client");
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        messageField = new JTextField(25);
        sendButton = new JButton("Send");
        privateButton = new JButton("Private");

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(chatArea));
        panel.add(messageField);
        panel.add(sendButton);
        panel.add(privateButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        sendButton.addActionListener((ActionListener) new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                client.sendMessage(message);
                messageField.setText("");
            }
        });
        privateButton.addActionListener((ActionListener) new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(panel, "Chat privado para :");
                if (!name.isEmpty()) {
                    client.privates(messageField.getText(), name);

                }
            }
        });
    }

    public void displayMessage(String message) {
        chatArea.append(message + "\n");
    }
}
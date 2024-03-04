package client;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClientUI {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton btnPrivado;

    private UDPClient client;
    private JComboBox<String> userComboBox;
    private List<DataUsers> usuarios;

    public ChatClientUI(UDPClient client) {
        this.client = client;
        createUI();
        usuarios = new ArrayList<>();

    }

    public void setUsers(String data) {
        usuarios.clear();
        String[] rep = data.split(",");

        for (String user : rep) {

            DataUsers u = new DataUsers();
            u.setUsername(user.split(":")[0]);
            u.setIP(user.split(":")[1]);
            u.setPuerto(user.split(":")[2]);
            usuarios.add(u);
        }
        List<String> usenames = new ArrayList<>();
        for (DataUsers usuario : usuarios) {
            usenames.add(usuario.getUsername());
        }
        updateUsersList(usenames.toArray(new String[0]));
    }

    private void createUI() {
        frame = new JFrame("Chat Client");
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        messageField = new JTextField(25);
        sendButton = new JButton("Send");
        btnPrivado = new JButton("Sala Privada");

        userComboBox = new JComboBox<>();

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(chatArea));
        panel.add(messageField);
        panel.add(sendButton);
        panel.add(userComboBox);
        panel.add(btnPrivado);

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
        btnPrivado.addActionListener((ActionListener) new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = (String) userComboBox.getSelectedItem();
                if (selectedUser != null) {
                    for (DataUsers dataUsers : usuarios) {
                        if (dataUsers.getUsername() == selectedUser) {
                            client.sendMessage("JOINPRIVADA:" + dataUsers.getIP() + ":" + dataUsers.getPuerto());
                        }
                    }
                }
            }
        });
    }

    public void updateUsersList(String[] users) {
        userComboBox.removeAllItems();
        for (String user : users) {
            userComboBox.addItem(user);
        }
    }

    public void displayMessage(String message) {
        chatArea.append(message + "\n");
    }
}
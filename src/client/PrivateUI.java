package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PrivateUI {
    private JFrame frame;
    private JTextArea privateChatArea;
    private JTextField messageField; // Nuevo campo de texto para escribir mensajes
    private JButton sendButton; // Nuevo botón para enviar mensajes
    private UDPClient client;
    private String userDEST;
    String userDESTIP;
    private String roomKey;

    public String getUserDESTIP() {
        return userDESTIP;
    }

    public void setUserDESTIP(String userDESTIP) {
        this.userDESTIP = userDESTIP;
    }

    public PrivateUI(String username, String roomkey, UDPClient client) {
        this.client = client;
        this.userDEST = username;
        this.roomKey = roomkey;

        frame = new JFrame("Private Chat with " + username);
        privateChatArea = new JTextArea(20, 30);
        privateChatArea.setEditable(false);

        messageField = new JTextField(20);
        sendButton = new JButton("Enviar");

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(privateChatArea));
        panel.add(messageField);
        panel.add(sendButton);

        frame.add(panel);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                accionAntesCerrar();
            }
        });

        // Acción para enviar mensajes al hacer clic en el botón
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensajePrivado();
            }
        });

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void displayMessage(String message) {
        privateChatArea.append(message + "\n");
    }

    private void accionAntesCerrar() {
        int opcion = JOptionPane.showConfirmDialog(frame, "¿Estás seguro de que quieres cerrar la ventana?",
                "Confirmar Cierre", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            String username = System.getProperty("user.name");
            client.sendMessage("EXITPRIVADA:" + username + ":" + userDEST + ":" + roomKey);
            frame.dispose();
        }
    }

    private void enviarMensajePrivado() {
        String message = messageField.getText();
        if (!message.isEmpty()) {

            // client.sendMessageToPrivateUser(userDEST, message, roomKey, senderUsername);
            client.sendMessage("PRIVADA:" + userDESTIP.split("/")[0] + ":" + userDESTIP.split("/")[1] + ":" +
                    message + ":" + roomKey); // Limpiar el campo de texto después de enviar el mensaje
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public JTextArea getPrivateChatArea() {
        return privateChatArea;
    }

    public String getRoomKey() {
        return roomKey;
    }
}

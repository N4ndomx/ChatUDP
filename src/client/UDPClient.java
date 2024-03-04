package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private ChatClientUI ui;
    private Map<String, PrivateUI> privateChatUIs; // Mapa para almacenar interfaces privadas por usuario
    private String username = System.getProperty("user.name");

    public UDPClient(String serverIP, int serverPort) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverIP);
        this.serverPort = serverPort;
        this.ui = new ChatClientUI(this);
        this.privateChatUIs = new HashMap<>();

        // Enviar mensaje de conexión
        sendMessage("CONNECT:" + username);
    }

    public void sendMessage(String message) {
        try {
            System.out.println("Enviando mensaje: " + message);
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessages() {
        new Thread(() -> {
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                    String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("recibi esto -> " + receivedMessage);
                    if (receivedMessage.startsWith("JOINPRIVADA")) {
                        String userdestino = receivedMessage.split(":")[1];
                        String userkeydest = receivedMessage.split(":")[2];

                        String roomkey = receivedMessage.split(":")[3];

                        openPrivateChatUI(userdestino, roomkey, userkeydest);

                    } else if (receivedMessage.startsWith("USERS")) {
                        String[] us = receivedMessage.split("/");
                        ui.setUsers(eliminarComando(us));
                    }
                    // Boolean n = receivedMessage.startsWith("PRIVADA");
                    // String n = receivedMessage.split(":")[0];
                    else if (receivedMessage.startsWith("PRIVADA")) {
                        handlePrivateMessage(receivedMessage);
                    } else {
                        ui.displayMessage(receivedMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void openPrivateChatUI(String privateUser, String roomkey, String keyDes) {

        PrivateUI privateUI = new PrivateUI(privateUser, roomkey, this);
        privateChatUIs.put(roomkey, privateUI);
        privateUI.setUserDESTIP(keyDes);
    }

    private void handlePrivateMessage(String receivedMessage) {
        String[] parts = receivedMessage.split(":");

        String privateUser = parts[1];
        String message = parts[2];
        String roomkey = parts[3];

        PrivateUI privateUI = privateChatUIs.get(roomkey);
        if (privateUI != null) {
            privateUI.displayMessage(privateUser + ": " + message);
        }

    }

    public String eliminarComando(String[] array) {
        String[] newArray = new String[array.length - 1];

        // Copiar los elementos excepto el primero
        System.arraycopy(array, 1, newArray, 0, newArray.length);

        StringBuilder stringBuilder = new StringBuilder();
        for (String element : newArray) {
            stringBuilder.append(element);
        }

        return stringBuilder.toString();

    }

    public void disconnect() {
        // Enviar mensaje de desconexión
        sendMessage("DISCONNECT");
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        UDPClient client = new UDPClient("192.168.1.64", 12345);
        client.receiveMessages();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.disconnect();
        }));
    }
}

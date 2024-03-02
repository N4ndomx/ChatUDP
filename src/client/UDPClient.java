package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private ChatClientUI ui;
    PrivateUI uip;

    public UDPClient(String serverIP, int serverPort) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverIP);
        this.serverPort = serverPort;
        this.ui = new ChatClientUI(this);

        // Enviar mensaje de conexión
        sendMessage("CONNECT");
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
                    if (receivedMessage.startsWith("JOINPRIVADA")) {
                        uip = new PrivateUI(this);

                    }
                    if (receivedMessage.startsWith("PRIVADA")) {
                        uip.displayMessage(receivedMessage);
                    }
                    ui.displayMessage(receivedMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disconnect() {
        // Enviar mensaje de desconexión
        sendMessage("DISCONNECT");
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        UDPClient client = new UDPClient("192.168.1.68", 12345);
        client.receiveMessages();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.disconnect();
        }));
    }
}
package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class UDPServer {
    private DatagramSocket socket;
    private Map<String, InetAddress> clients = new HashMap<>();
    private String _IP = "192.168.1.65"; // Cambie por su IP

    public UDPServer(int port) throws SocketException, UnknownHostException {
        InetAddress ip = InetAddress.getByName(_IP);
        socket = new DatagramSocket(port, ip);
    }

    private void handleConnection(InetAddress clientAddress, int clientPort) throws IOException {

        String clientKey = clientAddress.getHostAddress() + "/" + clientPort;
        clients.put(clientKey, clientAddress);

        System.out.println("Cliente conectado: " + clientKey);

        // Enviar notificación a todos los clientes
        broadcast("Cliente " + clientKey + " se ha conectado");

    }

    private void handleDisconnection(InetAddress clientAddress, int clientPort) throws IOException {
        String clientKey = clientAddress.getHostAddress() + "/" + clientPort;
        clients.remove(clientKey);

        System.out.println("Cliente desconectado: " + clientKey);

        // Enviar notificación a todos los clientes
        broadcast("Cliente " + clientKey + " se ha desconectado");
    }

    public void receiveData() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());
        // Procesar los datos recibidos...
    }

    public void sendData(String message, InetAddress address, int port) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

    public void processData(DatagramPacket packet) throws IOException {
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Mensaje recibido: " + message);
        InetAddress clientAddress = packet.getAddress();
        int clientPort = packet.getPort();
        String cadena = message.split(":")[0];

        String clientKey = clientAddress.getHostAddress() + "/" + clientPort;

        if (message.startsWith("CONNECT")) {
            handleConnection(clientAddress, clientPort);
        } else if (message.startsWith("DISCONNECT")) {
            handleDisconnection(clientAddress, clientPort);
        } else if (cadena.startsWith("PRIVATE")) {
            String ipDir = message.split(":")[1];
            String msg = message.split(":")[2];
            sendPrivateMessage(clientKey + ": " + msg, ipDir);
            // metodo para mandar mensaje privado
        } else {
            broadcast(clientKey + ": " + message);
        }
        // printClients();
    }

    public void broadcast(String message) throws IOException {
        for (String clientKey : clients.keySet()) {
            InetAddress clientAddress = clients.get(clientKey);

            int clientPort = Integer.parseInt(clientKey.split("/")[1]);
            sendData(message, clientAddress, clientPort);
        }
    }

    public void printClients() throws IOException {
        for (String clientKey : clients.keySet()) {
            InetAddress clientAddress = clients.get(clientKey);

            int clientPort = Integer.parseInt(clientKey.split("/")[1]);
            System.out.println("Cliente : " + clientAddress.toString() + " / " + clientPort);
        }
    }

    public void sendPrivateMessage(String message, String recipientKey) throws IOException {
        InetAddress recipientAddress = clients.get(recipientKey);
        if (recipientAddress != null) {
            int recipientPort = Integer.parseInt(recipientKey.split("/")[1]);

            sendData(message, recipientAddress, recipientPort);
        }
    }


    public void listen() {
        System.out.println("Servidor iniciado. Escuchando en el puerto: " + socket.getLocalPort() + " "
                + socket.getLocalAddress());

        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);

                // Procesar el paquete recibido
                processData(packet);
            } catch (IOException e) {
                System.err.println("Error en la conexión: " + e.getMessage());
                // Manejar adecuadamente la excepción
            }
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        int port = 1234; // Puedes cambiar esto al puerto que desees
        try {
            UDPServer server = new UDPServer(port);
            server.listen();
        } catch (SocketException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}

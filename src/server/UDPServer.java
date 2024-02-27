package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
// import java.util.Random;

public class UDPServer {
    private DatagramSocket socket;
    private Map<String, String> clients = new HashMap<>(); //maneja clientes activos del chat
    private static final String _IP = "10.40.4.47"; //dirección IP del servidor
    // private static Random rand = new Random();

    public UDPServer(int port) throws SocketException, UnknownHostException {
       InetAddress ip = InetAddress.getByName(_IP);
       socket = new DatagramSocket(port, ip);
    }

    //Maneja nuevas conexiones de clientes en el servidor UDP
    private void handleConnection(InetAddress clientAddress /*ip cliente*/, int clientPort /*puerto cliente*/, String username) throws IOException {
        String clientKey = clientAddress.getHostAddress() + ":" + clientPort; //creación de una clave única para el cliente combinando IP y puerto
        String user = username + clientPort;
        clients.put(clientKey, user); //agregación de la IP del cliente a la colección clients usando la clave generada

        System.out.println("Usuario conectado: " + user);

        // Enviar notificación a todos los clientes
        broadcast( user + " se ha conectado"); 
    }

    //Encargado de manejar las desconexiones de los clientes en un servidor UDP
    private void handleDisconnection(InetAddress clientAddress, int clientPort, String username) throws IOException {
        String clientKey = clientAddress.getHostAddress() + ":" + clientPort; //creación de una clave única para el cliente combinando IP y puerto
        String user = username + clientPort;
        clients.remove(clientKey); //eliminación del cliente de la colección clients utilizando la clave generada

        System.out.println("Usuario desconectado: " + user);

        // Enviar notificación a todos los clientes
        broadcast( user + " se ha desconectado");
    }

    //recibe datos de los clientes. Utiliza un buffer de 1024 bytes para recibir los datos
    public void receiveData() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());
        // Procesar los datos recibidos...
    }

    //envia datos a una dirección IP y puerto específicos.
    public void sendData(String message, InetAddress address, int port) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

    /*Procesa los paquetes DatagramPacket recibidos. Extrae el mensaje del paquete y lo imprime en la consola.
      Obtiene la dirección IP y el puerto del remitente para identificar al cliente.
      Diferencia entre mensajes de conexión, desconexión y mensajes regulares
    */
    public void processData(DatagramPacket packet) throws IOException {
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Mensaje recibido: " + message);
        InetAddress clientAddress = packet.getAddress();
        int clientPort = packet.getPort();
        String username = System.getProperty("user.name");

        String user = username + clientPort;

        if (message.startsWith("CONNECT")) {
            handleConnection(clientAddress, clientPort, username);
        } else if (message.startsWith("DISCONNECT")) {
            handleDisconnection(clientAddress, clientPort, username);
        } else {
            // Retransmitir el mensaje a todos los clientes, incluyendo la IP del remitente
            broadcast(user + ": " + message);
        }
    }

    // envia un mensaje a todos los clientes conectados al servidor
    public void broadcast(String message) throws IOException {
        for (String clientKey : clients.keySet()) {
            InetAddress clientAddress = InetAddress.getByName(clients.get(clientKey.split(":")[0]));
            
            int clientPort = Integer.parseInt(clientKey.split(":")[1]);
            sendData(message, clientAddress, clientPort);
        }
    }

    // envia un mensaje privado a un cliente específico identificado por una clave única 
    public void sendPrivateMessage(String message, String recipientKey) throws IOException {
        InetAddress recipientAddress = InetAddress.getByName(clients.get(recipientKey.split(":")[0]));
        if (recipientAddress != null) {
            int recipientPort = Integer.parseInt(recipientKey.split(":")[1]);
            sendData(message, recipientAddress, recipientPort);
        }
    }

    // inicia el proceso de escucha del servidor. 
    public void listen() {
        System.out.println("Servidor iniciado. Escuchando en el puerto: " + socket.getLocalPort());

        while (true) { //bucle infinito para escuchar continuamente los paquetes entrantes.
            try {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet); //espera los datos

                // Procesar el paquete recibido
                processData(packet); 
            } catch (IOException e) {
                System.err.println("Error en la conexión: " + e.getMessage());
                // Manejar adecuadamente la excepción
            }
        }
    }

    // punto de entrada de la aplicación del servidor.
    public static void main(String[] args) throws UnknownHostException {
/*      String sport = "";
        for (int i=0; i<5; i++){
            sport = sport + String.valueOf(rand.nextInt(rand.nextInt(9 - 1 + 1) + 1));
        } */
        int port = 12345;
        try {
            UDPServer server = new UDPServer(port);
            server.listen();
        } catch (SocketException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}

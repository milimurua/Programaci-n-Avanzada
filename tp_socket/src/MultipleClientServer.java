import java.io.*;
import java.net.*;

public class MultipleClientServer{
    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Servidor multicliente iniciado en el puerto " + port);

            while (true) {
                Socket socketCliente = server.accept();
                System.out.println("Nuevo cliente conectado: " + socketCliente.getInetAddress());

                ClientManager manager = new ClientManager(socketCliente);
                Thread hilo = new Thread(manager);
                hilo.start();
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}


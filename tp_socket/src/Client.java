import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente de chat que se conecta al servidor, envía/recibe mensajes
 * y perdir acciones al servidor
 */
public class Client {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final String name;

    public Client(String host, int port, String name) throws IOException {
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.name = name;
        sendName();
    }

    private void sendName() throws IOException {
        writer.write(name);
        writer.newLine();
        writer.flush();
    }

    public void start() {
        new Thread(this::listen).start();
        writeMessages();
    }

    private void listen() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                if ("bye".equalsIgnoreCase(message.trim())) {
                    System.out.println("Conexión cerrada por el servidor.");
                    break;
                }
                System.out.println(message);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo mensajes: " + e.getMessage());
        } finally {
            closeClient();
        }
    }

    private void writeMessages() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Escribe /ayuda para ver los comandos disponibles.");
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                writer.write(message);
                writer.newLine();
                writer.flush();
                if ("exit".equalsIgnoreCase(message.trim())) break;
            }
        } catch (IOException e) {
            System.err.println("Error enviando mensaje: " + e.getMessage());
        } finally {
            closeClient();
        }
    }

    private void closeClient() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando cliente: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Ingresa tu nombre: ");
            String name = sc.nextLine();
            Client client = new Client("localhost", 5000, name);
            client.start();
        } catch (IOException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }
}

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Chat server that accepts clients and executes ClientHandler on a thread pool.
 * Handles exceptions per connection to keep server running.
 */
public class Server {
    private static final int PORT = 5000;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado en el puerto " + PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());
                    pool.execute(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    System.err.println("Error aceptando conexión: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
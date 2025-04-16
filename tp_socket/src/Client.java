import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;

        try (Socket socket = new Socket(host, port);
             BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to server from" + host + ":" + port);
            String message;

            do {
                System.out.print("Send a message (o 'exit' for closed): ");
                message = buffer.readLine();
                output.println(message);

                String respuesta = input.readLine();
                System.out.println("server response: " + respuesta);

            } while (!message.equalsIgnoreCase("exit"));

            System.out.println("Cliente closed.");

        } catch (IOException e) {
            System.err.println("Error client: " + e.getMessage());
        }
    }
}

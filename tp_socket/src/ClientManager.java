import java.io.*;
import java.net.*;

public class ClientManager implements Runnable {
    private Socket socket;

    public ClientManager(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String message;
            while ((message = input.readLine()) != null) {
                System.out.println("Client [" + socket.getInetAddress() + "]: " + message);
                if (message.equalsIgnoreCase("exit")) {
                    output.println("Close Connection from client");
                    break;
                }
                output.println("Recive message: " + message);
            }

            socket.close();
            System.out.println("Client [" + socket.getInetAddress() + "] disconnected");

        } catch (IOException e) {
            System.err.println("Error with client " + socket.getInetAddress() + ": " + e.getMessage());
        }
    }
}

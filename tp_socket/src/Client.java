import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            String question = input.readLine();
            System.out.println(question);

            String instruction = input.readLine();
            System.out.println(instruction);

            String userAnswer = scanner.nextLine();
            output.println(userAnswer);

            String result = input.readLine();
            System.out.println(result);

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}


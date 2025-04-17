import java.io.*;
import java.net.*;
import java.util.*;

public class MultiClientServer {
    private static final int PORT = 5000;
    private static final List<Question> questions = Arrays.asList(
            new Question("What is the capital of France?", "Paris"),
            new Question("What is 5 + 7?", "12"),
            new Question("What color is the sky on a clear day?", "Blue"),
            new Question("Which continent is Argentina in?", "America"),
            new Question("What is 9 * 3?", "27")
    );

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String userResponse;

                do {
                    Question question = questions.get(new Random().nextInt(questions.size()));
                    output.println("Question: " + question.getText());
                    output.println("Type your answer (or 'exit' to quit):");

                    userResponse = input.readLine();
                    if (userResponse == null || userResponse.equalsIgnoreCase("exit")) {
                        output.println("Connection closed. Goodbye!");
                        break;
                    } else if (userResponse.trim().equalsIgnoreCase(question.getAnswer())) {
                        output.println("✅ Correct!");
                    } else {
                        output.println("❌ Incorrect. Correct answer was: " + question.getAnswer());
                    }

                } while (true);

            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    System.out.println("Client disconnected: " + socket.getInetAddress());
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }

    private static class Question {
        private final String text;
        private final String answer;

        public Question(String text, String answer) {
            this.text = text;
            this.answer = answer;
        }

        public String getText() {
            return text;
        }

        public String getAnswer() {
            return answer;
        }
    }
}

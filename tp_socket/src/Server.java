import java.io.*;
import java.net.*;
import java.sql.SQLOutput;

public class Server {
    public static void main(String[] arg){
        int port = 5000;

        try(ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server init");

            Socket socketClient = server.accept();
            System.out.println("Client connected from: "+ socketClient.getInetAddress());

            BufferedReader input = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            PrintWriter output = new PrintWriter(socketClient.getOutputStream(), true);

            String message;

            while((message = input.readLine()) != null){
                System.out.println("Cliente: "+ message);
                if(message.equalsIgnoreCase("exit")){
                    output.println("end");
                    break;
                }
                output.println("Message recived: "+ message);
            }
            socketClient.close();
            System.out.println("Server closed");
        } catch (IOException e) {
            throw new RuntimeException("Error server: "+e.getMessage());
        }
    }
}

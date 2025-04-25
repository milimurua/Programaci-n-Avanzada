import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente de chat que se conecta al servidor y envía/recibe mensajes
 */
public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    /**
     * Constructor: inicializa las conexiones y flujos.
     */
    public Client(Socket socket, String name) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.name = name;
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Envía el nombre y luego lee mensajes desde la consola para enviarlos.
     */
    public void setMessage(){
        try {
            // Envía el nombre al servidor para registrarse
            bufferedWriter.write(name);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(name + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Escucha en un hilo separado los mensajes que llegan del servidor.
     */
    public void listenForMessage(){ //recibir y leer los mensajes
        new Thread(new Runnable(){
            @Override
            public void run() {
                String messageFromChat;

                while (socket.isConnected()){
                    try {
                        messageFromChat = bufferedReader.readLine();
                        System.out.println(messageFromChat);
                    }catch (IOException e){
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    /**
     * Cierra socket y flujos de E/S.
     */
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{ //cierra todas las conexiones
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();;
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa tu nombre: ");
        String name = scanner.nextLine();
        Socket socket = new Socket("localhost", 5000);
        Client client = new Client(socket, name);
        client.listenForMessage();
        client.setMessage();

    }
}

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;


public class ClientHandler implements Runnable {

    //Lista de todos los handlers para establecer las conexióones
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;                  //Establece la conexión entre el cliente y el servidor.
    private BufferedReader bufferedReader;  //Leer los mensajes del cliente.
    private BufferedWriter bufferedWriter;  //Enviar mensajes a otros clientes
    private String clientName;              //Identificador con el cliente

    /**
     * Contructor: configura los flujos de E/S y el cliente
     */
    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            //Inicializa el escritor para enviar datos al cliente
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //Inicializa el lector para recibir datos del cliente
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.clientName = bufferedReader.readLine();
            //añade a la lista
            clientHandlers.add(this);
            //avisa a los otros clientes cuando uno nuevo ingresa
            broadcastMessage("Server: "+ clientHandlers + " entró al chat");
            sendPrivate("Bienvenido al chat, " + clientName + ". Escribe /ayuda para ver los comandos disponibles.");
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
            throw new RuntimeException("Error al inicializar el handler de cliente", e);
        }
    }

    /**
     * Metodo principal del hilo: escucha y difunde mensajes del cliente
     */
    @Override
    public void run() {
        try {
            String message;
            while ((message = bufferedReader.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    sendPrivate("bye");
                    break;
                } else if (message.startsWith("/")) {
                    handleCommand(message);
                } else {
                    broadcastMessage(clientName + ": " + message);
                }
            }
        } catch (IOException e) {
            System.err.println("Error en la comunicación con " + clientName + ": " + e.getMessage());
        } finally {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void handleCommand(String command) throws IOException {
        switch (command) {
            case "/ayuda":
                sendPrivate("Comandos disponibles:\n" +
                        " - /ayuda: muestra esta ayuda\n" +
                        " - /fecha: muestra la fecha actual\n" +
                        " - /hora: muestra la hora actual\n" +
                        " - /usuarios: muestra los usuarios conectados\n" +
                        " - exit: salir del chat");
                break;
            case "/fecha":
                sendPrivate("Fecha actual: " + LocalDate.now());
                break;
            case "/hora":
                sendPrivate("Hora actual: " +
                        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                break;
            case "/usuarios":
                StringBuilder sb = new StringBuilder("Usuarios conectados:\n");
                for (ClientHandler handler : clientHandlers) {
                    sb.append(" - ").append(handler.clientName).append("\n");
                }
                sendPrivate(sb.toString());
                break;
            default:
                sendPrivate("Comando no reconocido. Escribe /ayuda para ver la lista de comandos.");
        }
    }

    /**
     * nuevo agregado: Envía un mensaje solo a este cliente (mensaje privado).
     */
    private void sendPrivate(String message) {
        try {
            bufferedWriter.write("Servidor: " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Envía un mensaje a todos los clientes conectados (excepto al emisor).
     */
    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler : clientHandlers){
            try {
                //evita que se envie al cliente que envió el mensaje
                if (!clientHandler.clientName.equals(clientName)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }


    /**
     * Elimina este handler de la lista y notifica la salida del cliente.
     */
    public void removeClientHandler() { //cierra la conexión del cliente
        clientHandlers.remove(this);
        broadcastMessage("Server: " + clientName + " se fue del chat");
    }

    /**
     * Cierra Socket y el flujo de mensajes
     */
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
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

}

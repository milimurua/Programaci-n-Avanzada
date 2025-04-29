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
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Metodo principal del hilo: escucha y difunde mensajes del cliente
     */
    @Override
    public void run() {
        String messageFromClient;
        //si la conexión está activa
        while(socket.isConnected()){
            try {
                //Lee mensaje enviados por el cliente
                messageFromClient = bufferedReader.readLine();

                //AGREGADO DE COMANDOS....
                if (messageFromClient == null) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
                if (messageFromClient.contains("/ayuda")) {//Genero el comando ayuda, envia un mensaje con los comandos disponibles

                    //sendPrivateMessage: envia un mensaje solo al cliente que lo ejecuto
                    sendPrivateMessage(
                            "Comandos disponibles:\n" +
                                    " - /ayuda : muestra esta ayuda\n" +
                                    " - /fecha : muestra la fecha actual\n" +
                                    " - /hora : muestra la hora actual\n" +
                                    " - /usuarios : muestra los usuarios conectados"
                    );
                    //Genero el comando Fecha
                } else if (messageFromClient.contains("/fecha")) {  //Genero el comando Fecha
                    LocalDate fechaActual = LocalDate.now();
                    sendPrivateMessage("Fecha actual: " + fechaActual.toString());

                } else if (messageFromClient.contains("/hora")) {  //Genero el comando Hora
                    LocalTime horaActual = LocalTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    sendPrivateMessage("Hora actual: " + horaActual.format(formatter));

                } else if (messageFromClient.contains("/usuarios")) { //Genero el comando Usuarios
                    StringBuilder usuarios = new StringBuilder("Usuarios conectados:\n");
                    // mira todos los clientes conectados y agrega sus nombres a 'usuarios'
                    for (ClientHandler ch : clientHandlers) {
                        usuarios.append("- ").append(ch.clientName).append("\n");
                    }
                    sendPrivateMessage(usuarios.toString());
                } else {
                    //Envia un mensaje a todos
                    broadcastMessage(messageFromClient);
                }


            }catch (IOException e) {
                //cierra recursos y termina el bucle
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
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
    public void removeClientHandler(){ //cierra la conexión del cliente
        clientHandlers.remove(this);
        broadcastMessage("Server"+ clientName + "se fue del chat");
    }

    /**
     * nuevo agregado: Envía un mensaje solo a este cliente (mensaje privado).
     */
    private void sendPrivateMessage(String mensaje) {
        try {
            bufferedWriter.write("Servidor: " + mensaje);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
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

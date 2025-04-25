import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

//runnable: hará que las instancias se ejecuten mediante un hilo separado.
public class ClientHandler implements Runnable{

    //arrayLits: realizar un seguimiento de todos nuestros clientes, resposable de la comunicación entre clientes.
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket; //Establece la conexión entre el cliente y el servidor.
    private BufferedReader bufferedReader; //Leer los mensajes del cliente
    private BufferedWriter bufferedWriter; //Enviar mensajes a otros clientes
    private String clientName;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientName = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("Server: "+ clientHandlers + " entró al chat");
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler : clientHandlers){
            try {
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

    public void removeClientHandler(){ //cierra la conexión del cliente
        clientHandlers.remove(this);
        broadcastMessage("Server"+ clientName + "se fue del chat");
    }

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

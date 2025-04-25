import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    //método encargado que de que corra el server
    public void startServer(){
        try { //Manejo de errores de entrada y salida
            while(!serverSocket.isClosed()){
                Socket socket =  serverSocket.accept(); //permitimos la conexión de un nuevo cliente
                System.out.println("Se conectó un nuevo cliente");
                ClientHandler clientHandler = new ClientHandler(socket); //instaciamos la clase clientHandler

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){

        }
    }

    public void closeServerSocket(){
        try{
            if (serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        Server server = new Server(serverSocket);
        System.out.println("basda");
        server.startServer();

    }
}


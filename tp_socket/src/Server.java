import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    private ServerSocket serverSocket;

    //Constructor: recibe un ServerSocket configurado para inicializar el servidor
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Inicia el bucle principal que acepta conexiones entrantes
     */
    public void startServer(){
        try {
            //mientras el socket no esté cerrado, aceptamos conexiones
            while(!serverSocket.isClosed()){
                //permitimos la conexión de un nuevo cliente
                Socket socket =  serverSocket.accept()
                        ;
                System.out.println("Se conectó un nuevo cliente");

                //instaciamos la clase clientHandler
                ClientHandler clientHandler = new ClientHandler(socket);

                //Ejecuta el manejador de hilos independiente
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){
            //En caso de error de E/S, se puede loguear o manejar la excepción
            System.err.println("Error al aceptar conexiones: " + e.getMessage());
        }
    }

    /**
     * Cierra el serverSocket para dejar de aceptar conexiones
     */
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
        ServerSocket serverSocket = new ServerSocket(5000);  //crea el serverSocket
        Server server = new Server(serverSocket);
        System.out.println("Server iniciado");
        server.startServer(); //ejecuta el método de conexiones
    }
}


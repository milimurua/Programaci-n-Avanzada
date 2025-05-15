import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ClientHandler implements Runnable {
    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private final Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Asignar nombre único
            String originalName = reader.readLine();
            String uniqueName = originalName;
            int suffix = 1;
            synchronized (clientHandlers) {
                while (nameExists(uniqueName)) {
                    uniqueName = originalName + suffix++;
                }
                clientName = uniqueName;
                clientHandlers.add(this);
            }
            if (!uniqueName.equals(originalName)) {
                sendPrivate("El nombre '" + originalName + "' ya estaba en uso. Te asigné '" + uniqueName + "'.");
            }

            broadcastMessage("Servidor: " + clientName + " entró al chat");
            sendPrivate("Bienvenido al chat, " + clientName + ". Escribe /ayuda para ver los comandos disponibles.");
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    sendPrivate("bye");
                    break;
                } else if (message.startsWith("/")) {
                    handleCommand(message);
                } else {
                    // Mensaje normal: difundir sin incluir al emisor y aclarar que es para todos
                    broadcastMessage(clientName + " (para todos): " + message);
                }
            }
        } catch (IOException e) {
            System.err.println("Error en comunicación con " + clientName + ": " + e.getMessage());
        } finally {
            closeEverything();
        }
    }

    private void handleCommand(String fullCommand) throws IOException {
        String[] parts = fullCommand.split("\\s+", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "/ayuda":
                sendPrivate("Comandos disponibles:\n" +
                        " - /ayuda: muestra esta ayuda\n" +
                        " - /fecha: muestra la fecha actual\n" +
                        " - /hora: muestra la hora actual\n" +
                        " - /calculadora <expresión>: calcalcula suma y resta (solo operaciones + y -)\n" +
                        " - /usuarios: muestra los usuarios conectados\n" +
                        " - /mensaje <usuarios> <mensaje>: mensaje privado\n" +
                        " - exit: cerrar la conexión");
                break;

            case "/fecha":
                sendPrivate("Fecha actual: " + LocalDate.now());
                break;

            case "/hora":
                sendPrivate("Hora actual: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                break;

            case "/calculadora":
                if (args.isEmpty()) {
                    sendPrivate("Uso: /calculadora <expresión>");
                } else {
                    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
                    Object result;
                    if (engine != null) {
                        try {
                            result = engine.eval(args);
                        } catch (Exception e) {
                            result = "Error: " + e.getMessage();
                        }
                    } else {
                        result = evalSimple(args);
                    }
                    sendRaw("Servidor: Resultado: " + result);
                }
                break;

            case "/usuarios":
                StringBuilder sb = new StringBuilder("Usuarios conectados:\n");
                synchronized (clientHandlers) {
                    for (ClientHandler ch : clientHandlers) {
                        sb.append(" - ").append(ch.clientName).append("\n");
                    }
                }
                sendPrivate(sb.toString());
                break;

            case "/mensaje":
                if (!args.contains(" ")) {
                    sendPrivate("Uso: /mensaje <usuarios separados por ','> <mensaje>");
                } else {
                    String[] mu = args.split("\\s+", 2);
                    String targets = mu[0], body = mu[1];
                    for (String user : targets.split(",")) {
                        boolean found = false;
                        synchronized (clientHandlers) {
                            for (ClientHandler h : clientHandlers) {
                                if (h.clientName.equals(user)) {
                                    h.sendRaw(clientName + " (privado): " + body);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            sendPrivate("Servidor: Usuario '" + user + "' no existe.");
                        }
                    }
                }
                break;

            default:
                sendPrivate("Comando no reconocido. Escribe /ayuda para ver la lista de comandos.");
        }
    }

    private boolean nameExists(String name) {
        for (ClientHandler ch : clientHandlers) {
            if (ch.clientName.equals(name)) return true;
        }
        return false;
    }

    private void sendPrivate(String message) throws IOException {
        writer.write("Servidor: " + message);
        writer.newLine();
        writer.flush();
    }

    private void sendRaw(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    private void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler ch : clientHandlers) {
                if (ch != this) {
                    try {
                        ch.writer.write(message);
                        ch.writer.newLine();
                        ch.writer.flush();
                    } catch (IOException e) {
                        // ignorar fallo de envío
                    }
                }
            }
        }
    }

    private String evalSimple(String expr) {
        try {
            String e = expr.replaceAll("\\s+", "");
            if (e.matches("\\d+[+-]\\d+")) {
                if (e.contains("+")) {
                    String[] parts = e.split("\\+");
                    return String.valueOf(
                            Integer.parseInt(parts[0]) + Integer.parseInt(parts[1])
                    );
                } else {
                    String[] parts = e.split("\\-");
                    return String.valueOf(
                            Integer.parseInt(parts[0]) - Integer.parseInt(parts[1])
                    );
                }
            }
        } catch (Exception ignored) {}
        return "Expresión no soportada";
    }

    private void closeEverything() {
        try {
            synchronized (clientHandlers) {
                clientHandlers.remove(this);
            }
            broadcastMessage("Servidor: " + clientName + " se desconectó");
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando conexión de " + clientName + ": " + e.getMessage());
        }
    }
}
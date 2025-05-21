package com.socket;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private final Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Leer nombre y hacerlo único, y registrar este handler
            String originalName = reader.readLine();
            clientName = makeUnique(originalName);

            // Notificaciones iniciales
            broadcastMessage("Servidor: " + clientName + " entró al chat");
        } catch (IOException e) {
            closeEverything();
        }
    }

    /**
     * Genera un nombre único y añade este handler al listado
     */
    private String makeUnique(String originalName) {
        String unique = originalName;
        int suffix = 1;
        synchronized (clientHandlers) {
            while (nameExists(unique)) {
                unique = originalName + suffix++;
            }
            clientHandlers.add(this);
        }
        return unique;
    }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = reader.readLine()) != null) {
                if (msg.equalsIgnoreCase("exit")) {
                    sendToThis("Servidor: chau");
                    break;
                } else if (msg.startsWith("/")) {
                    handleCommand(msg);
                } else {
                    broadcastMessage(clientName + " (todos): " + msg);
                }
            }
        } catch (IOException e) {
            System.err.println("Error con " + clientName + ": " + e.getMessage());
        } finally {
            closeEverything();
        }
    }

    private void handleCommand(String fullCommand) throws IOException {
        String[] parts = fullCommand.split("\\s+", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "/fecha":
                sendToThis("Servidor: Fecha actual: " + LocalDate.now());
                break;
            case "/hora":
                sendToThis("Servidor: Hora actual: "
                        + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                break;
            case "/calculadora":
                if (args.isBlank()) {
                    sendToThis("Servidor: Uso: /calculadora <expresión>");
                } else {
                    ScriptEngine engine = new ScriptEngineManager()
                            .getEngineByName("JavaScript");
                    Object result;
                    try {
                        result = (engine != null)
                                ? engine.eval(args)
                                : evalSimple(args);
                    } catch (Exception ex) {
                        result = "Error: " + ex.getMessage();
                    }
                    sendToThis("Servidor: Resultado: " + result);
                }
                break;
            case "/usuarios":
                StringBuilder sb = new StringBuilder("Servidor: Usuarios conectados:\n");
                synchronized (clientHandlers) {
                    for (ClientHandler ch : clientHandlers) {
                        sb.append(" - ").append(ch.clientName).append("\n");
                    }
                }
                sendToThis(sb.toString());
                break;
            case "/mensaje":
                if (!args.contains(" ")) {
                    sendToThis("Servidor: Uso: /mensaje <user1,user2> <mensaje>");
                } else {
                    String[] mu = args.split("\\s+", 2);
                    for (String user : mu[0].split(",")) {
                        boolean found = false;
                        synchronized (clientHandlers) {
                            for (ClientHandler h : clientHandlers) {
                                if (h.clientName.equals(user)) {
                                    h.sendToThis(clientName + " (privado): " + mu[1]);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            sendToThis("Servidor: Usuario '" + user + "' no existe.");
                        }
                    }
                }
                break;
        }
    }

    private boolean nameExists(String name) {
        for (ClientHandler ch : clientHandlers) {
            if (ch.clientName.equals(name)) return true;
        }
        return false;
    }

    /**
     * Envía un mensaje únicamente a este cliente
     */
    private void sendToThis(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException ignored) {
        }
    }

    /**
     * Envía un mensaje a todos menos al emisor
     */
    private void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler ch : clientHandlers) {
                if (ch != this) {
                    ch.sendToThis(message);
                }
            }
        }
    }

    /**
     * Evalúa expresiones simples
     */
    private String evalSimple(String expr) {
        String e = expr.replaceAll("\\s+", "");
        try {
            if (e.matches("\\d+[+-]\\d+")) {
                if (e.contains("+")) {
                    String[] p = e.split("\\+");
                    return String.valueOf(
                            Integer.parseInt(p[0]) + Integer.parseInt(p[1]));
                } else {
                    String[] p = e.split("\\-");
                    return String.valueOf(
                            Integer.parseInt(p[0]) - Integer.parseInt(p[1]));
                }
            }
        } catch (Exception ignored) {
        }
        return "Expresión no soportada";
    }

    /**
     * Cierra todo y notifica desconexión
     */
    private void closeEverything() {
        try {
            synchronized (clientHandlers) {
                clientHandlers.remove(this);
            }
            broadcastMessage("Servidor: " + clientName + " se desconectó");
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando conexión de "
                    + clientName + ": " + e.getMessage());
        }
    }
}

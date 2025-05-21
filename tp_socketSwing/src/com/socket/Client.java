package com.socket;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class Client {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final Consumer<String> onMessage;

    public Client(String host, int port, String name, Consumer<String> onMessage) throws IOException {
        this.socket    = new Socket(host, port);
        this.reader    = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer    = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.onMessage = onMessage;

        // enviar nombre
        writer.write(name);
        writer.newLine();
        writer.flush();
    }

    public void connect() {
        new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    onMessage.accept(line);
                }
            } catch (IOException e) {
                onMessage.accept("** Conexión perdida: " + e.getMessage());
            }
        }).start();
    }

    public void send(String msg) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            onMessage.accept("** Error enviando: " + e.getMessage());
        }
    }

    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

}

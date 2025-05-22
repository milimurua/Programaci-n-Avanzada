package com.socket.ui;

import com.socket.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ChatUI extends JFrame {
    private final JTextArea taLog    = new JTextArea();
    private final JTextField tfInput = new JTextField();
    private final JButton    btnSend  = new JButton("Enviar");
    private final JToolBar   toolbar  = new JToolBar();
    private Client           client;

    public ChatUI(String host, int port, String name) {
        super("Chat: " + name);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //el usuario cierra todo
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
        setLayout(new BorderLayout(5,5));
        setSize(600, 450);

        // Barra de botones de comandos (ya no mostramos estados aquí)
        toolbar.setFloatable(false);
        addCommandButton("Fecha",    () -> client.send("/fecha"));
        addCommandButton("Hora",     () -> client.send("/hora"));
        addCommandButton("Usuarios", () -> client.send("/usuarios"));
        addCommandButton("Calc",     () -> {
            String expr = JOptionPane.showInputDialog(this, "Expresión (+/-):", "123+45");
            if (expr != null) client.send("/calculadora " + expr.trim());
        });
        addCommandButton("Privado",  this::openPrivateChatDialog);
        add(toolbar, BorderLayout.NORTH);

        // Área de mensajes
        taLog.setEditable(false);
        add(new JScrollPane(taLog), BorderLayout.CENTER);

        // Panel de entrada con botón Enviar
        JPanel south = new JPanel(new BorderLayout(5,5));
        south.add(tfInput, BorderLayout.CENTER);
        south.add(btnSend, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);
        btnSend.setEnabled(true);

        // Conectar en segundo plano, sin mostrar texto de estado
        new Thread(() -> {
            try {
                client = new Client(host, port, name, this::append);
                client.connect();
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                                "No pude conectar al servidor:\n" + ex.getMessage(),
                                "Error de conexión",
                                JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();

        // Envío desde campo de texto o botón
        ActionListener send = e -> {
            String txt = tfInput.getText().trim();
            if (!txt.isEmpty()) {
                if ("exit".equalsIgnoreCase(txt)) {
                    shutdown();
                } else {
                    client.send(txt);
                    tfInput.setText("");
                }
            }
        };
        btnSend.addActionListener(send);
        tfInput.addActionListener(send);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static String validateUsername(String s) {
        if (s.length() < 3) {
            return "Debe tener al menos 3 caracteres.";
        }
        if (s.length() > 12) {
            return "No puede superar 12 caracteres.";
        }
        for (char c : s.toCharArray()) {
            if (!(Character.isLetterOrDigit(c) || c == '_')) {
                return "Sólo letras, numeros o '_'.";
            }
        }
        if (s.contains(" ")) {
            return "No puede contener espacios.";
        }
        return null;
    }


    private void addCommandButton(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.addActionListener(e -> action.run());
        toolbar.add(btn);
    }

    private void openPrivateChatDialog() {
        String target = JOptionPane.showInputDialog(
                this, "Usuario destino:", "Privado", JOptionPane.PLAIN_MESSAGE);
        if (target == null || target.trim().isEmpty()) return;
        String msg = JOptionPane.showInputDialog(
                this, "Mensaje para " + target + ":", "Privado", JOptionPane.PLAIN_MESSAGE);
        if (msg != null && !msg.trim().isEmpty()) {
            client.send("/mensaje " + target.trim() + " " + msg.trim());
        }
    }

    private void append(String msg) {
        SwingUtilities.invokeLater(() -> {
            taLog.append(msg + "\n");
            taLog.setCaretPosition(taLog.getDocument().getLength());
        });
    }

    private void shutdown() {
        if (client != null) {
            try {
                client.send("exit");   // opcional: avisar al servidor
                client.disconnect();   // implementa este método para cerrar streams y socket
            } catch (IOException ignored) {}
        }
        dispose();
        System.exit(0);
    }


    public static void main(String[] args) {
        String name;
        // Bucle hasta que el usuario introduzca un nombre válido o cancele
        while (true) {
            name = JOptionPane.showInputDialog("Tu nombre (3–12 chars, sólo letras/dígitos/_):");
            if (name == null) {
                // Canceló
                System.exit(0);
            }
            name = name.trim();
            String error = validateUsername(name);
            if (error == null) {
                break;  // OK
            }
            // Si no es válido, muestro mensaje y vuelvo a pedir
            JOptionPane.showMessageDialog(
                    null, error, "Nombre inválido", JOptionPane.ERROR_MESSAGE
            );
        }

        final String user = name;
        SwingUtilities.invokeLater(() ->
                new ChatUI("localhost", 5000, user)
        );
    }
}



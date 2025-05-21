package com.formulario.swing;

import com.formulario.swing.FormContactPanel;
import javax.swing.*;
import java.awt.*;

public class Main{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Formulario de Contacto");
            FormContactPanel panel = new FormContactPanel();

            JButton btnValidate = new JButton("Validar");
            JButton btnClean = new JButton("Limpiar");
            JButton btnClose  = new JButton("Cerrar");

            btnValidate.addActionListener(e -> panel.validateAndShow());
            btnClean.addActionListener(e -> panel.cleanFields());
            btnClose.addActionListener(e -> frame.dispose());

            JPanel south = new JPanel();
            south.add(btnValidate);
            south.add(btnClean);
            south.add(btnClose);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel, BorderLayout.CENTER);
            frame.add(south,  BorderLayout.SOUTH);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

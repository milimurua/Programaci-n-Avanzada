package com.formulario.swing;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FormContactPanel extends JPanel {
    private JTextField tfName       = new JTextField();
    private JTextField tfSurname    = new JTextField();
    private JTextField tfDni        = new JTextField();
    private JTextField tfPassport   = new JTextField();
    private JTextField tfPhone      = new JTextField();
    private JTextField tfZP         = new JTextField();
    private JTextField tfResidency  = new JTextField();

    private JLabel lblDNI    = new JLabel("DNI:");
    private JLabel lblPas    = new JLabel("Pasaporte:");

    private JRadioButton rbDNI       = new JRadioButton("DNI", true);
    private JRadioButton rbPassport  = new JRadioButton("Pasaporte");

    public FormContactPanel() {
        setLayout(new GridLayout(0,2,5,5));
        add(new JLabel("Nombre:"));    add(tfName);
        add(new JLabel("Apellido:"));  add(tfSurname);

        // Radios
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbDNI);
        bg.add(rbPassport);
        add(rbDNI);
        add(rbPassport);

        // Etiquetas + campos controlados por toggleIdFields
        add(lblDNI);     add(tfDni);
        add(lblPas);     add(tfPassport);

        // Resto de campos
        add(new JLabel("Teléfono:"));    add(tfPhone);
        add(new JLabel("Cód. Postal:")); add(tfZP);
        add(new JLabel("Domicilio:"));   add(tfResidency);

        // Inicializa visibilidad según radio seleccionado
        toggleIdFields();

        // Listeners para cambiar visibilidad
        rbDNI      .addActionListener(e -> toggleIdFields());
        rbPassport .addActionListener(e -> toggleIdFields());
    }

    private void toggleIdFields() {
        boolean esDNI = rbDNI.isSelected();
        lblDNI .setVisible(esDNI);
        tfDni  .setVisible(esDNI);
        lblPas .setVisible(!esDNI);
        tfPassport.setVisible(!esDNI);
        // limpia el campo no visible
        if (esDNI) {
            tfPassport.setText("");
        } else {
            tfDni.setText("");
        }
        revalidate();
        repaint();
    }

    /** Rellena un DTO Contact con los valores de la UI. */
    public Contact getContactoFromFields() {
        Contact c = new Contact();
        c.name      = tfName.getText().trim();
        c.surname   = tfSurname.getText().trim();
        c.dni       = tfDni.getText().trim();
        c.passport  = tfPassport.getText().trim();
        c.phone     = tfPhone.getText().trim();
        c.zipCode   = tfZP.getText().trim();
        c.residency = tfResidency.getText().trim();
        return c;
    }

    /** Ejecuta la validación y muestra errores o éxito. */
    public void validateAndShow() {
        Contact c = getContactoFromFields();
        List<String> errores = Validation.validate(c);
        if (errores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "✔ Datos válidos", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    String.join("\n", errores),
                    "Errores de validación",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void cleanFields() {
        tfName      .setText("");
        tfSurname   .setText("");
        tfDni       .setText("");
        tfPassport  .setText("");
        tfPhone     .setText("");
        tfZP        .setText("");
        tfResidency .setText("");
        rbDNI.setSelected(true);
        toggleIdFields();
    }
}

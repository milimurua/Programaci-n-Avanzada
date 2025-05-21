package com.formulario.swing;

import java.util.ArrayList;
import java.util.List;

public class Validation {

    public static List<String> validate(Contact c) {
        List<String> errors = new ArrayList<>();

        // Sólo letras en nombre y apellido
        if (!onlyLetters(c.name)) {
            errors.add("Nombre sólo debe contener letras.");
        }
        if (!onlyLetters(c.surname)) {
            errors.add("Apellido sólo debe contener letras.");
        }

        boolean dniExist      = c.dni      != null && !c.dni.isBlank();
        boolean passportExist = c.passport != null && !c.passport.isBlank();

        // Longitud exacta de DNI
        if (dniExist && c.dni.length() != 8) {
            errors.add("DNI debe tener exactamente 8 dígitos.");
        }

        // Validación de pasaporte: 1 letra mayúscula + 8 dígitos
        if (passportExist) {
            String p = c.passport;
            if (p.length() != 9
                    || !Character.isUpperCase(p.charAt(0))
                    || !allDigits(p.substring(1))) {
                errors.add("Pasaporte debe tener 1 letra mayúscula seguida de 8 dígitos.");
            }
        }

        // Teléfono: mínimo 6 caracteres y sólo dígitos, espacios, + ( ) -
        if (c.phone == null || c.phone.length() < 6 || !allAllowedPhoneChars(c.phone)) {
            errors.add("Teléfono inválido.");
        }

        // Código postal: exactamente 4 dígitos
        if (c.zipCode == null || c.zipCode.length() != 4 || !allDigits(c.zipCode)) {
            errors.add("Código postal debe tener 4 dígitos.");
        }

        return errors;
    }

    /** Comprueba carácter a carácter que la cadena sólo contenga letras */
    private static boolean onlyLetters(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isLetter(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Comprueba que todos los caracteres sean dígitos */
    private static boolean allDigits(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char ch : s.toCharArray()) {
            if (!Character.isDigit(ch)) return false;
        }
        return true;
    }

    /**
     * Comprueba que el teléfono solo contenga caracteres permitidos:
     * dígitos, espacios, +, paréntesis o guiones.
     */
    private static boolean allAllowedPhoneChars(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char ch : s.toCharArray()) {
            if (!(Character.isDigit(ch)
                    || ch == '+'
                    || ch == '('
                    || ch == ')'
                    || ch == '-'
                    || ch == ' ')) {
                return false;
            }
        }
        return true;
    }
}

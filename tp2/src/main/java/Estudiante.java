import java.util.*;

public class Estudiante extends Persona {
    private List<Nota> notas;

    public Estudiante(String nombre, String dni) {
        super(nombre, dni);
        this.notas = new ArrayList<>();
    }

    public void agregarNota(String tema, double valor) {
        notas.add(new Nota(tema, valor));
    }

    public double promedio() {
        if (notas.isEmpty()) return 0;
        double suma = 0;
        for (Nota n : notas) {
            suma += n.valor;
        }
        return suma / notas.size();
    }

    public void listarNotas() {
        for (Nota n : notas) {
            System.out.println(" - " + n.tema + ": " + n.valor);
        }
    }

    @Override
    public String rol() {
        return "Estudiante";
    }

    public class Nota {
        String tema;
        double valor;

        public Nota(String tema, double valor) {
            this.tema = tema;
            this.valor = valor;
        }
    }
}

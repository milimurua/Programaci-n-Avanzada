import java.util.*;

public class Curso {
    private String nombre;
    private Profesor profesor;
    private List<Estudiante> estudiantes;

    public Curso(String nombre, Profesor profesor) {
        this.nombre = nombre;
        this.profesor = profesor;
        this.estudiantes = new ArrayList<>();
    }

    public void inscribirEstudiante(Estudiante e) {
        estudiantes.add(e);
        System.out.println(e.getNombre() + " inscrito en el curso " + nombre);
    }

    public void listarEstudiantes() {
        System.out.println("Estudiantes en " + nombre + ":");
        for (Estudiante e : estudiantes) {
            System.out.println(" - " + e.getNombre());
        }
    }
}

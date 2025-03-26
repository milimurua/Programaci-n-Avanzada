public class Profesor extends Persona {
    private String especialidad;

    public Profesor(String nombre, String dni, String especialidad) {
        super(nombre, dni);
        this.especialidad = especialidad;
    }

    public Curso crearCurso(String nombreCurso) {
        return new Curso(nombreCurso, this);
    }

    @Override
    public String rol() {
        return "Profesor";
    }
}

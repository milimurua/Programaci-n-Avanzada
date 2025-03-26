public abstract class Persona {
    protected String nombre;
    protected String dni;

    public Persona(String nombre, String dni) {
        this.nombre = nombre;
        this.dni = dni;
    }

    public abstract String rol();

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }
}

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Crear profesor
        System.out.print("Nombre del profesor: ");
        String nombreProf = sc.nextLine();
        System.out.print("DNI del profesor: ");
        String dniProf = sc.nextLine();
        System.out.print("Especialidad: ");
        String esp = sc.nextLine();

        Profesor prof = new Profesor(nombreProf, dniProf, esp);

        // Crear curso
        System.out.print("Nombre del curso a crear: ");
        String nombreCurso = sc.nextLine();
        Curso curso = prof.crearCurso(nombreCurso);

        // Crear estudiantes en bucle hasta que se decida parar
        while (true) {
            System.out.print("\n¿Desea agregar un nuevo estudiante? (s para sí / cualquier otra tecla para salir): ");
            String continuar = sc.nextLine();
            if (!continuar.equalsIgnoreCase("s"))
                break;

            System.out.print("Nombre del estudiante: ");
            String nombreEst = sc.nextLine();
            System.out.print("DNI: ");
            String dniEst = sc.nextLine();
            Estudiante e = new Estudiante(nombreEst, dniEst);
            curso.inscribirEstudiante(e);

            System.out.print("¿Desea ingresar notas para este estudiante? (s/n): ");
            if (sc.nextLine().equalsIgnoreCase("s")) {
                System.out.print("¿Cuántas notas?: ");
                int nNotas = Integer.parseInt(sc.nextLine());
                for (int j = 0; j < nNotas; j++) {
                    System.out.print("Tema: ");
                    String tema = sc.nextLine();
                    System.out.print("Valor: ");
                    double valor = Double.parseDouble(sc.nextLine());
                    e.agregarNota(tema, valor);
                }
                System.out.println("Promedio: " + e.promedio());
            }
        }

        System.out.println("\nResumen del curso:");
        curso.listarEstudiantes();
    }
}

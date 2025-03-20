
//Generar una serie de 500 números enteros aleatorios entre 10 y 1000
//Calcular el promedio de estos números así como también la suma total de los mismos.
//Mostrar el resultado de estos valores obtenidos
import java.util.Random;

public class Main {
  public static void main(String[] args) {
    Random random = new Random();
    int[] numbers = new int[500];
    int sum = 0;

    for (int i = 0; i < 500; i++) {
      numbers[i] = random.nextInt(1000 - 10) + 10;
      sum += numbers[i];
      
    }

    double average = (double) sum / 500;

    System.out.println("Promedio: " + average);
    System.out.println("Suma total: " + sum);
  }
}
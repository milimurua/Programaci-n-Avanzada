package session;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SessionManager manager = SessionManager.getInstance();

        System.out.println("=== LOGIN ===");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        User user = new User(name, email);
        manager.login(user);
        manager.showSession();

        System.out.println("------------------------");

        // Logout
        manager.logout();
        manager.showSession();

        System.out.println("------------------------");

        // Bucle para preguntar si quiere reabrir
        String answer = "";
        while (true) {
            System.out.print("Do you want to reopen your session? (yes/no): ");
            answer = scanner.nextLine().trim().toLowerCase();

            if (answer.equals("yes") || answer.equals("y")) {
                manager.reopenLastSession();
                manager.showSession();

                // Cierra la sesión de nuevo para seguir probando el bucle
                manager.logout();
                System.out.println("------------------------");

            } else if (answer.equals("no") || answer.equals("n")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Please enter 'yes' or 'no'.");
            }
        }

        scanner.close();
    }
}

package pharmacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Order> orders = new ArrayList<>();

        System.out.println("=== Welcome to the Pharmacy Order System ===");

        while (true) {
            System.out.println("\nSelect a medication:");
            System.out.println("1. Amoxicillin 500mg ($8.99) [Antibiotic]");
            System.out.println("2. Ibuprofen 400mg ($5.50) [Painkiller]");
            System.out.println("3. Cetirizine 10mg ($4.25) [Allergy]");
            System.out.print("Enter your choice (1-3): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // clear buffer

            String name = "";
            double price = 0;

            switch (choice) {
                case 1 -> { name = "Amoxicillin 500mg [Antibiotic]"; price = 8.99; }
                case 2 -> { name = "Ibuprofen 400mg [Painkiller]"; price = 5.50; }
                case 3 -> { name = "Cetirizine 10mg [Allergy]"; price = 4.25; }
                default -> {
                    System.out.println("Invalid option.");
                    continue;
                }
            }

            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // clear buffer

            Order order = new Medication(name, price, quantity);
            boolean hasPrescription = false;

            System.out.print("Add prescription processing ($1.25/unit, -$1.00/unit discount)? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                order = new Prescription(order);
                hasPrescription = true;
            }

            System.out.print("Add thermal packaging ($2.00/unit)? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                order = new ThermalPackaging(order);
            }

            System.out.print("Add home delivery ($3.50 once)? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                order = new Delivery(order);
            }

            orders.add(order);
            System.out.println("Product added successfully!");
            if (hasPrescription) {
                System.out.printf("You saved $%.2f with your prescription!\n", 1.00 * quantity);
            }

            System.out.print("\nDo you want to add another product? (y/n): ");
            String again = scanner.nextLine().toLowerCase();
            if (!again.equals("y")) break;
        }

        // Summary
        System.out.println("\n=== 🧾 Final Order Summary ===");
        double total = 0;
        int i = 1;
        for (Order o : orders) {
            System.out.println("[" + i++ + "] " + o.getDescription() + " => $" + String.format("%.2f", o.getCost()));
            total += o.getCost();
        }
        System.out.printf("TOTAL: $%.2f\n", total);

        scanner.close();
    }
}

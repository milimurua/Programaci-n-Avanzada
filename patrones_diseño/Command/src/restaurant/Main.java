package restaurant;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Waiter waiter = new Waiter();
        Chef chef = new Chef();
        CommandManager manager = new CommandManager();
        Scanner scanner = new Scanner(System.in);

        List<String> menu = Arrays.asList("Pizza Margherita", "Spaghetti Carbonara", "Lasagna", "Risotto", "Tiramisu");
        Random random = new Random();

        boolean running = true;
        while (running) {
            System.out.println("\n=== Restaurant Command System ===");
            System.out.println("1. Client chooses random dish");
            System.out.println("2. Waiter adds order manually");
            System.out.println("3. Waiter cancels random order");
            System.out.println("4. Show all orders");
            System.out.println("5. Undo last action");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    String dish = menu.get(random.nextInt(menu.size()));
                    Order newOrder = new Order(dish);
                    manager.executeCommand(new AddCommand(waiter, newOrder));
                    chef.prepareOrder(newOrder, () -> {
                        synchronized (System.out) {
                            System.out.println();
                        }
                    });
                    try { Thread.sleep(2100); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                case 2 -> {
                    System.out.print("Enter dish name: ");
                    String name = scanner.nextLine();
                    Order newOrder = new Order(name);
                    manager.executeCommand(new AddCommand(waiter, newOrder));
                    chef.prepareOrder(newOrder, () -> {
                        synchronized (System.out) {
                            System.out.println();
                        }
                    });
                    try { Thread.sleep(2100); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                case 3 -> {
                    Order randomCancel = waiter.getRandomOrder();
                    if (randomCancel != null) {
                        manager.executeCommand(new CancelCommand(waiter, randomCancel));
                    } else {
                        System.out.println("No orders to cancel.");
                    }
                }
                case 4 -> {
                    System.out.println("\nCurrent Orders:");
                    for (Order o : waiter.getOrders()) {
                        System.out.println(" - " + o);
                    }
                }
                case 5 -> manager.undoLast();
                case 0 -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }
}
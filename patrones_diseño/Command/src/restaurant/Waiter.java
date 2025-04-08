package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Waiter {
    private List<Order> orders = new ArrayList<>();

    public void addOrder(Order order) {
        orders.add(order);
        System.out.println("Order added: " + order.getDishName());
    }

    public void cancelOrder(Order order) {
        if (orders.remove(order)) {
            System.out.println("Order canceled: " + order.getDishName());
        } else {
            System.out.println("Order not found: " + order.getDishName());
        }
    }

    public Order getRandomOrder() {
        if (orders.isEmpty()) return null;
        Random rand = new Random();
        return orders.get(rand.nextInt(orders.size()));
    }

    public List<Order> getOrders() {
        return orders;
    }
}
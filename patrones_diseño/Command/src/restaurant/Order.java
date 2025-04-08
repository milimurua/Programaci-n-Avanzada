package restaurant;

import java.time.LocalTime;

public class Order {
    private String dishName;
    private boolean ready;
    private LocalTime readyTime;

    public Order(String dishName) {
        this.dishName = dishName;
        this.ready = false;
    }

    public String getDishName() {
        return dishName;
    }

    public boolean isReady() {
        return ready;
    }

    public void markReady() {
        this.ready = true;
        this.readyTime = LocalTime.now();
    }

    public LocalTime getReadyTime() {
        return readyTime;
    }

    @Override
    public String toString() {
        return dishName + (ready ? " (Ready at " + readyTime + ")" : " (Pending)");
    }
}

package restaurant;

import java.util.Timer;
import java.util.TimerTask;

public class Chef {
    private Runnable onReadyCallback;

    public void prepareOrder(Order order, Runnable onReadyCallback) {
        this.onReadyCallback = onReadyCallback;
        System.out.println("Preparing: " + order.getDishName());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                order.markReady();
                System.out.println("\nOrder ready: " + order.getDishName() + " at " + order.getReadyTime());
                if (onReadyCallback != null) {
                    onReadyCallback.run();
                }
            }
        }, 2000);
    }
}
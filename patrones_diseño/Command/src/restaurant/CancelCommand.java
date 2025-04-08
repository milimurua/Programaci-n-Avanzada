package restaurant;

public class CancelCommand implements Command{
    private Waiter waiter;
    private Order order;

    public CancelCommand(Waiter waiter, Order order) {
        this.waiter = waiter;
        this.order = order;
    }

    @Override
    public void execute() {
        waiter.cancelOrder(order);
    }

    @Override
    public void undo() {
        waiter.addOrder(order);
    }
}

package restaurant;

public class AddCommand implements Command{
    private Waiter waiter;
    private Order order;

    public AddCommand(Waiter waiter, Order order) {
        this.waiter = waiter;
        this.order = order;
    }

    @Override
    public void execute() {
        waiter.addOrder(order);
    }

    @Override
    public void undo() {
        waiter.cancelOrder(order);
    }
}

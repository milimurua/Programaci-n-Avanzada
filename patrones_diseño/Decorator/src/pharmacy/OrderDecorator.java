package pharmacy;

public abstract class OrderDecorator implements Order {
    protected Order order;

    public OrderDecorator(Order order) {
        this.order = order;
    }

    @Override
    public String getDescription() {
        return order.getDescription();
    }

    @Override
    public double getCost() {
        return order.getCost();
    }
}


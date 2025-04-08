package pharmacy;

public class Delivery extends OrderDecorator {
    private static final double EXTRA_COST = 3.50;

    public Delivery(Order order) {
        super(order);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Home Delivery";
    }

    @Override
    public double getCost() {
        return super.getCost() + EXTRA_COST;
    }
}


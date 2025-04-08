package pharmacy;

public class Prescription extends OrderDecorator {
    private static final double PROCESSING_COST = 1.25;
    private static final double DISCOUNT_PER_UNIT = 1.00;

    public Prescription (Order order) {
        super(order);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Prescription Processing (-$1.00/unit discount)";
    }

    @Override
    public double getCost() {
        double cost = super.getCost();
        if (order instanceof Medication m) {
            double discount = DISCOUNT_PER_UNIT * m.getQuantity();
            double processing = PROCESSING_COST * m.getQuantity();
            return cost + processing - discount;
        }
        return cost + PROCESSING_COST; // fallback
    }
}



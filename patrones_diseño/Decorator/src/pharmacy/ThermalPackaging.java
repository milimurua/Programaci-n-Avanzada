package pharmacy;

public class ThermalPackaging extends OrderDecorator {
    public ThermalPackaging(Order order) {
        super(order);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Thermal Packaging";
    }

    @Override
    public double getCost() {
        return super.getCost() + 2.00;
    }
}

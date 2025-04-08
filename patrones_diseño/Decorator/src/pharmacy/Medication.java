package pharmacy;

public class Medication implements Order {
    private String name;
    private double unitPrice;
    private int quantity;

    public Medication(String name, double unitPrice, int quantity) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    @Override
    public String getDescription() {
        return quantity + "x " + name;
    }

    @Override
    public double getCost() {
        return unitPrice * quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}



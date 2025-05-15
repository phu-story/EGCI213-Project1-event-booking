package project1;

public class Discount {
    private double minSubTotal;
    private double discountPercent;

    public Discount(double minSubTotal, double discountPercent) {
        this.minSubTotal = minSubTotal;
        this.discountPercent = discountPercent;
    }

    public double getMinSubTotal() {
        return minSubTotal;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }
}

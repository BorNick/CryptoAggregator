package base;

public class BaseDepthItem {

    /**
     * The price of the base depth item
     */
    public double price;

    /**
     * The amount of currency
     */
    public double amount;

    /**
     *
     */
    public BaseDepthItem() {
    }

    /**
     *
     * @param price The price of the base depth item
     * @param amount The amount of currency
     */
    public BaseDepthItem(double price, double amount) {
        this.price = price;
        this.amount = amount;
    }

    /**
     *
     * @return
     */
    public String toString() {
        return "price = " + this.price
                + "; amount = " + this.amount + ";";
    }
}

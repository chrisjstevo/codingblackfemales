package codingblackfemales.sotw;

import messages.order.Side;

public class ChildOrder {
    private Side side;
    private long orderId;
    private long quantity;
    private long price;

    private long filledQuantity;
    private int state;

    public ChildOrder(Side side, long orderId, long quantity, long price, long filledQuantity, int state) {
        this.side = side;
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
        this.filledQuantity = filledQuantity;
        this.state = state;
    }

    public Side getSide() {
        return side;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getPrice() {
        return price;
    }

    public long getFilledQuantity() {
        return filledQuantity;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setFilledQuantity(long filledQuantity) {
        this.filledQuantity = filledQuantity;
    }
}

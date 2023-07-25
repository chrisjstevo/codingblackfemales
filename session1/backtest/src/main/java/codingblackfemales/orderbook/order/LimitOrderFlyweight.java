package codingblackfemales.orderbook.order;

import messages.order.Side;

public class LimitOrderFlyweight extends DefaultOrderFlyweight {

    private Side side;
    private long price;
    private long quantity;
    private long orderId;

    public LimitOrderFlyweight(Side side, long price, long quantity, long orderId) {
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    public Side getSide() {
        return side;
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "Limit(side="+side+",orderId="+orderId+",price="+price+",quantity="+quantity+")";
    }
}

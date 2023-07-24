package codingblackfemales.orderbook.order;

public class LimitOrderFlyweight extends DefaultOrderFlyweight {
    private long price;
    private long quantity;
    private long orderId;

    public LimitOrderFlyweight(long price, long quantity, long orderId) {
        this.price = price;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public long getQuantity() {
        return quantity;
    }

    public long getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "Limit(orderId="+orderId+",price="+price+",quantity="+quantity+")";
    }
}

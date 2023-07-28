package codingblackfemales.orderbook.order;

import messages.order.Side;

public class MarketDataOrderFlyweight extends DefaultOrderFlyweight {
    private long price;
    private long quantity;

    private Side side;

    public MarketDataOrderFlyweight(Side side, long price, long quantity) {
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
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

    @Override
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "MktData(price="+price+",quantity="+quantity+")";
    }
}

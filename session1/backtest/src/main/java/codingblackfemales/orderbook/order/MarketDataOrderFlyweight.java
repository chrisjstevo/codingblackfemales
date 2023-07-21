package codingblackfemales.orderbook.order;

public class MarketDataOrderFlyweight extends AbstractOrderFlyweight<MarketDataOrderFlyweight>{
    private long price;
    private long quantity;

    public MarketDataOrderFlyweight(long price, long quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public long getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }
}

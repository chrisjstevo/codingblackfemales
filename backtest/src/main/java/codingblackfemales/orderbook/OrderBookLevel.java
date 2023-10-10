package codingblackfemales.orderbook;

import codingblackfemales.collection.intrusive.IntrusiveLinkedListNode;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import codingblackfemales.orderbook.visitor.OrderBookVisitor;

public class OrderBookLevel extends IntrusiveLinkedListNode<OrderBookLevel> {

    private long price;
    private long quantity;

    private DefaultOrderFlyweight firstOrder;


    public OrderBookLevel() {
        super();
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public void setFirstOrder(final DefaultOrderFlyweight order){
        this.firstOrder = order;
    }

    public DefaultOrderFlyweight getFirstOrder(){
        return this.firstOrder;
    }

    public void removeMarketDataOrder(){
        var order = this.firstOrder;
        while(order != null){
            if(order instanceof MarketDataOrderFlyweight){
                var marketDataOrder = (MarketDataOrderFlyweight) order;
                this.firstOrder = marketDataOrder.remove();
            }
            order = order.next();
        }
    }

    public void accept(OrderBookVisitor visitor, OrderBookSide side){
        visitor.visitLevel(side, this);

        DefaultOrderFlyweight order = firstOrder;

        if(firstOrder == null){
            firstOrder = visitor.onNoFirstOrder();
            return;
        }

        while(order != null){
            DefaultOrderFlyweight next = order.next();
            order.accept(visitor, side, this, next == null);
            order = next;
        }

    }


    @Override
    public String toString() {
        return "OrderBookLevel(price=" + price + ",quantity=" + quantity + ")" ;
    }
}

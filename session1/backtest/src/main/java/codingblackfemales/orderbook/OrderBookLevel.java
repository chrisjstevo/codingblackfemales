package codingblackfemales.orderbook;

import codingblackfemales.collection.intrusive.IntrusiveLinkedListNode;
import codingblackfemales.orderbook.order.*;
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

    public void removeMarketDataOrder(){
        var order = this.firstOrder;
        while(order != null){
            if(order instanceof MarketDataOrderFlyweight){
                var marketDataOrder = (MarketDataOrderFlyweight) order;
                marketDataOrder.remove();
            }
        }
    }

    public void accept(OrderBookVisitor visitor){
        visitor.visit(this);

        DefaultOrderFlyweight order = firstOrder;

        while(order != null){
            DefaultOrderFlyweight next = order.next();
            order.accept(visitor, next == null);
        }

    }


    @Override
    public String toString() {
        if(this.next() == null){
            return "OBLevel(price=" + price + ",quantity=" + quantity + ")" ;
        }else{
            return "OBLevel(price=" + price + ",quantity=" + quantity + ") -> " + this.next().toString();
        }
    }
}

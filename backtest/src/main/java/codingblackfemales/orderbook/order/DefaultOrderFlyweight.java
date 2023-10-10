package codingblackfemales.orderbook.order;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.visitor.OrderBookVisitor;
import messages.order.Side;

public class DefaultOrderFlyweight extends ParentOrderFlyweight<DefaultOrderFlyweight>{

    @Override
    public void accept(OrderBookVisitor visitor, OrderBookSide side, OrderBookLevel level, boolean isLast) {
        visitor.visitOrder(this, side, level, isLast);
    }

    public void setQuantity(long quantity){}


    public Side getSide() {
        return null;
    }

    public long getPrice() {
        return 0;
    }

    public long getQuantity() {
        return 0;
    }
}

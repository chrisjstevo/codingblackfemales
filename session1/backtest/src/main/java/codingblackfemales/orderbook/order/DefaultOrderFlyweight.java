package codingblackfemales.orderbook.order;

import codingblackfemales.orderbook.visitor.OrderBookVisitor;

public class DefaultOrderFlyweight extends ParentOrderFlyweight<DefaultOrderFlyweight>{

    @Override
    public void accept(OrderBookVisitor visitor, boolean isLast) {
        visitor.visit(this, isLast);
    }
}

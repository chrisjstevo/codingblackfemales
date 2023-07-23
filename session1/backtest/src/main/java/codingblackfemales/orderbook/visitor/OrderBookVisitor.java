package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.Order;

public interface OrderBookVisitor {
    void visit(OrderBookLevel level);
    void visit(DefaultOrderFlyweight order, boolean isLast);
    OrderBookLevel missingBookLevel(OrderBookLevel previous, OrderBookLevel next, long price);

    OrderBookLevel firstBookLevel(long price);

    long desiredPrice();

}

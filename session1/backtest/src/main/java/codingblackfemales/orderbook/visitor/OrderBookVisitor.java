package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;

public interface OrderBookVisitor {
    void visit(OrderBookLevel level);
    void visit(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast);
    OrderBookLevel missingBookLevel(OrderBookLevel previous, OrderBookLevel next, long price);

    OrderBookLevel onNoFirstLevel();

    DefaultOrderFlyweight onNoFirstOrder();

}

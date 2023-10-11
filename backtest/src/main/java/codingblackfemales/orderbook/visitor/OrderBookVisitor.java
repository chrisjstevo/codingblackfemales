package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;

public interface OrderBookVisitor {

    void visitSide(OrderBookSide side);
    void visitLevel(OrderBookSide side, OrderBookLevel level);
    void visitOrder(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast);
    OrderBookLevel missingBookLevel(OrderBookLevel previous, OrderBookLevel next, long price);
    OrderBookLevel onNoFirstLevel();
    DefaultOrderFlyweight onNoFirstOrder();

}

package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.order.Order;

public interface OrderBookVisitor {
    void visit(OrderBookLevel level);
    void visit(Order order);

}

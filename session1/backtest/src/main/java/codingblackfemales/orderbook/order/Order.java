package codingblackfemales.orderbook.order;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.visitor.OrderBookVisitor;

public interface Order {
    long getPrice();
    long getQuantity();

    void accept(OrderBookVisitor visitor, OrderBookSide side, OrderBookLevel level, boolean isLast);
}

package codingblackfemales.orderbook.order;

import codingblackfemales.orderbook.visitor.OrderBookVisitor;

public interface Order {
    long getPrice();
    long getQuantity();

    void accept(OrderBookVisitor visitor, boolean isLast);
}

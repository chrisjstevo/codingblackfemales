package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.Order;

public class MutatingAddOrderVisitor implements OrderBookVisitor,FilteringOrderBookVisitor{

    private Order orderToAdd;

    public Order getOrderToAdd() {
        return orderToAdd;
    }

    public void setOrderToAdd(Order orderToAdd) {
        this.orderToAdd = orderToAdd;
    }

    @Override
    public void visit(OrderBookLevel level) {
        level.setQuantity(level.getQuantity() + orderToAdd.getQuantity());
    }

    @Override
    public void visit(DefaultOrderFlyweight order, boolean isLast) {

    }

    @Override
    public OrderBookLevel missingBookLevel(OrderBookLevel previous, OrderBookLevel next, long price) {
        OrderBookLevel level = new OrderBookLevel();
        level.setPrice(price);
        return level;
    }

    @Override
    public OrderBookLevel firstBookLevel(long price) {
        OrderBookLevel level = new OrderBookLevel();
        level.setPrice(price);
        return level;
    }

    @Override
    public long desiredPrice() {
        return orderToAdd.getPrice();
    }

    @Override
    public boolean filter(OrderBookLevel level) {
        return false;
    }
}

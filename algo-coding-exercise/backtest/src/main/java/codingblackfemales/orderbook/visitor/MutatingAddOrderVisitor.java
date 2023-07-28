package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutatingAddOrderVisitor implements OrderBookVisitor,FilteringOrderBookVisitor{

    private static final Logger logger = LoggerFactory.getLogger(MutatingAddOrderVisitor.class);

    private DefaultOrderFlyweight orderToAdd;

    public Order getOrderToAdd() {
        return orderToAdd;
    }

    public void setOrderToAdd(DefaultOrderFlyweight orderToAdd) {
        this.orderToAdd = orderToAdd;
    }

    @Override
    public void visitSide(OrderBookSide side) {}

    @Override
    public void visitLevel(OrderBookSide side, OrderBookLevel level) {
        level.setQuantity(level.getQuantity() + orderToAdd.getQuantity());
    }

    @Override
    public void visitOrder(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast) {
        if(order.getPrice() == orderToAdd.getPrice() && isLast){
            logger.info("[ORDERBOOK] + " +order);
            order.add(orderToAdd);
        }
    }

    @Override
    public DefaultOrderFlyweight onNoFirstOrder() {
        return orderToAdd;
    }

    @Override
    public OrderBookLevel missingBookLevel(OrderBookLevel previous, OrderBookLevel next, long price) {
        OrderBookLevel level = new OrderBookLevel();
        level.setPrice(price);
        return level;
    }

    @Override
    public OrderBookLevel onNoFirstLevel() {
        OrderBookLevel level = new OrderBookLevel();
        level.setPrice(orderToAdd.getPrice());
        level.setQuantity(0);
        return level;
    }

    @Override
    public long getPrice() {
        return this.orderToAdd.getPrice();
    }
}

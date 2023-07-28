package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CancelOrderVisitor implements OrderBookVisitor{

    private Logger logger = LoggerFactory.getLogger(CancelOrderVisitor.class);

    private final long orderId;

    public CancelOrderVisitor(final long orderId) {
        this.orderId = orderId;
    }

    @Override
    public void visitSide(OrderBookSide side) {}

    @Override
    public void visitLevel(OrderBookSide side, OrderBookLevel level) {

    }

    @Override
    public void visitOrder(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast) {
        if(order instanceof LimitOrderFlyweight){
            LimitOrderFlyweight limit = (LimitOrderFlyweight) order;
            if(limit.getOrderId() == orderId){
                logger.info("[ORDERBOOK] Cancelling order: " + limit);
                level.setFirstOrder(limit.remove());
                level.setQuantity(level.getQuantity() - limit.getQuantity());
            }
        }
    }

    @Override
    public OrderBookLevel missingBookLevel(OrderBookLevel previous, OrderBookLevel next, long price) {
        return null;
    }

    @Override
    public OrderBookLevel onNoFirstLevel() {
        return null;
    }

    @Override
    public DefaultOrderFlyweight onNoFirstOrder() {
        return null;
    }
}

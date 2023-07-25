package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;

public class MutatingRemoveOneOrderVisitor implements OrderBookVisitor{

    private long orderId;

    public long getOrderIdToRemove() {
        return orderId;
    }

    public void setOrderIdToRemove(final long orderId) {
        this.orderId = orderId;
    }

    @Override
    public void visitLevel(OrderBookSide side, OrderBookLevel level) {}

    @Override
    public void visitSide(OrderBookSide side) {}

    @Override
    public void visitOrder(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast) {
        if(order instanceof LimitOrderFlyweight){
            LimitOrderFlyweight limit = (LimitOrderFlyweight) order;

            System.out.println("Checking of " + limit + "is the one I want to delete (orderId=" + orderId + ")");

            if(limit.getOrderId() == getOrderIdToRemove()){
                System.out.println("yes it is....");
                level.setFirstOrder(order.remove());
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

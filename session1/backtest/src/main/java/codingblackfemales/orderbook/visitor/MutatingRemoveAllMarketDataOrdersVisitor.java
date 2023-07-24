package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;

public class MutatingRemoveAllMarketDataOrdersVisitor implements OrderBookVisitor{

    @Override
    public void visit(OrderBookLevel level) {}

    @Override
    public void visit(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast) {
        if(order instanceof MarketDataOrderFlyweight){
            DefaultOrderFlyweight newFirst = order.remove();
            level.setFirstOrder(newFirst);
            System.out.println("Removing market data order:" + order);
            if(level.getQuantity() - order.getQuantity() == 0){
                System.out.println("Removing level:" + level.getPrice());
                side.setFirstLevel(level.remove());
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

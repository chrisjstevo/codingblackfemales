package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutatingRemoveAllMarketDataOrdersVisitor implements OrderBookVisitor{

    private static final Logger logger = LoggerFactory.getLogger(MutatingRemoveAllMarketDataOrdersVisitor.class);

    @Override
    public void visitLevel(OrderBookSide side, OrderBookLevel level) {}

    @Override
    public void visitOrder(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast) {
        if(order instanceof MarketDataOrderFlyweight){
            DefaultOrderFlyweight newFirst = order.remove();
            level.setFirstOrder(newFirst);
            logger.info("[ORDERBOOK] Removing market data order:" + order);
            if(level.getQuantity() - order.getQuantity() == 0){
                logger.info("[ORDERBOOK] Removing level:" + level.getPrice());
                OrderBookLevel newFirstLevel = level.remove();
                side.setFirstLevel(newFirstLevel);
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

    @Override
    public void visitSide(OrderBookSide side) {}
}

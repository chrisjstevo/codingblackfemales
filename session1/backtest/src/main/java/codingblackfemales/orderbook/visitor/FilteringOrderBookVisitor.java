package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;

public interface FilteringOrderBookVisitor extends OrderBookVisitor{

    public boolean filter(final OrderBookLevel level);

}

package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.order.Order;

public class MutatingAddOrderVisitor implements OrderBookVisitor{

    @Override
    public void visit(OrderBookLevel level) {

    }

    @Override
    public void visit(Order order) {

    }

    public void afterLastLevel(){

    }

    public void afterLastOrder(){

    }
}

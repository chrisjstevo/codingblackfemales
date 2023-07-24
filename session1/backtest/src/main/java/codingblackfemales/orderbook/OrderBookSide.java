package codingblackfemales.orderbook;

import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import codingblackfemales.orderbook.visitor.FilteringOrderBookVisitor;
import codingblackfemales.orderbook.visitor.MutatingAddOrderVisitor;
import codingblackfemales.orderbook.visitor.MutatingRemoveAllMarketDataOrdersVisitor;
import codingblackfemales.orderbook.visitor.OrderBookVisitor;
import messages.marketdata.BookUpdateDecoder;

public abstract class OrderBookSide {
    private OrderBookLevel firstLevel;

    private final MutatingRemoveAllMarketDataOrdersVisitor removeMarketDataOrderVisitor = new MutatingRemoveAllMarketDataOrdersVisitor();

    public boolean canMatch(OrderBookSide side, long quantity, long price){
        return false;
    }

    public OrderBookLevel getFirstLevel() {
        return firstLevel;
    }

    public void setFirstLevel(OrderBookLevel level) {
        firstLevel = level;
    }

    public void accept(final OrderBookVisitor visitor){

        visitor.visit(this);

        var levelToVisit = getFirstLevel();

        //are we the first level...
        if(levelToVisit == null){
            OrderBookLevel level = visitor.onNoFirstLevel();
            if(level != null) {
                setFirstLevel(level);
                level.accept(visitor, this);
            }
            return;
        }else{
            visitOneLevel(visitor, levelToVisit, levelToVisit.next());
        }

        levelToVisit = levelToVisit.next();

        while(levelToVisit != null){
            visitOneLevel(visitor, levelToVisit, levelToVisit.next());
            levelToVisit = levelToVisit.next();
        }
    }

    private void visitOneLevel(final OrderBookVisitor visitor, OrderBookLevel levelToVisit, OrderBookLevel nextLevel) {
        if (visitor instanceof FilteringOrderBookVisitor) {

            long priceToFind = ((FilteringOrderBookVisitor) visitor).getPrice();

            if (priceToFind == levelToVisit.getPrice()) {
                levelToVisit.accept(visitor, this);
            } else if (isBetweenLevels(levelToVisit, nextLevel, priceToFind)) {
                visitor.missingBookLevel(levelToVisit, nextLevel, priceToFind);
            } else if (isNewDeepestLevel(levelToVisit, nextLevel, priceToFind)) {
                OrderBookLevel level = visitor.missingBookLevel(levelToVisit, nextLevel, priceToFind);
                levelToVisit.next(level);
            }
        }else{
            levelToVisit.accept(visitor, this);
        }
    }



    abstract boolean isBetweenLevels(OrderBookLevel previous, OrderBookLevel next, long price);

    abstract boolean isNewDeepestLevel(OrderBookLevel previous, OrderBookLevel next, long price);

    void removeMarketDataOrders(){

        this.accept(removeMarketDataOrderVisitor);


//        var level = getFirstLevel();
//
//        if(level == null){
//            return;
//        }
//
//        level.removeMarketDataOrder();
//
//        while(level.next() != null){
//            level = level.next();
//            level.removeMarketDataOrder();
//        }
    }

    void addMarketDataOrder(MarketDataOrderFlyweight order){
        this.getAddOrderVisitor().setOrderToAdd(order);
        this.accept(this.getAddOrderVisitor());
    }

    void addLimitOrder(LimitOrderFlyweight order){
        this.getAddOrderVisitor().setOrderToAdd(order);
        this.accept(this.getAddOrderVisitor());
    }

    private void addMarketDataOrders(BookUpdateDecoder bookUpdateDecoder){
        for(BookUpdateDecoder.AskBookDecoder decoder : bookUpdateDecoder.askBook()) {
            final long price = decoder.price();
            final long quantity = decoder.size();
            System.out.println("Adding order price:" + price + " quantity:" + quantity);
            var marketOrder = new MarketDataOrderFlyweight(price, quantity);
            addMarketDataOrder(marketOrder);
        }
    }

    abstract MutatingAddOrderVisitor getAddOrderVisitor();

}

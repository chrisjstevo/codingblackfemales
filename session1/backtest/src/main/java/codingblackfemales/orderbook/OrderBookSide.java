package codingblackfemales.orderbook;

import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import codingblackfemales.orderbook.order.Order;
import codingblackfemales.orderbook.visitor.MutatingAddOrderVisitor;
import codingblackfemales.orderbook.visitor.OrderBookVisitor;
import codingblackfemales.sequencer.Sequencer;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;

abstract class OrderBookSide {
    private OrderBookLevel firstLevel;

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
        var levelToVisit = getFirstLevel();
        long priceToFind = visitor.desiredPrice();

        //are we the first level...
        if(levelToVisit == null){
            OrderBookLevel level = visitor.firstBookLevel(visitor.desiredPrice());
            setFirstLevel(level);
            level.accept(visitor);
            return;
        }

        OrderBookLevel nextLevel = levelToVisit.next();

        while(levelToVisit != null){
            //exactly equal price
            if(priceToFind == levelToVisit.getPrice()){
                levelToVisit.accept(visitor);
            }else if(isBetweenLevels(levelToVisit, nextLevel, priceToFind)){
                visitor.missingBookLevel(levelToVisit, nextLevel, priceToFind);
            }else if(isNewDeepestLevel(levelToVisit, nextLevel, priceToFind)){
                OrderBookLevel level = visitor.missingBookLevel(levelToVisit, nextLevel, priceToFind);
                levelToVisit.next(level);
            }

            levelToVisit = levelToVisit.next();
            if(levelToVisit != null) {
                nextLevel = levelToVisit.next();
            }else{
                nextLevel = null;
            }
        }
    }

    abstract boolean isBetweenLevels(OrderBookLevel previous, OrderBookLevel next, long price);

    abstract boolean isNewDeepestLevel(OrderBookLevel previous, OrderBookLevel next, long price);

    void removeMarketDataOrders(){

        var level = getFirstLevel();

        if(level == null){
            return;
        }

        level.removeMarketDataOrder();

        while(level.next() != null){
            level = level.next();
            level.removeMarketDataOrder();
        }
    }

    void addMarketDataOrder(MarketDataOrderFlyweight order){
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

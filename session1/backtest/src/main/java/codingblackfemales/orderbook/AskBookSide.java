package codingblackfemales.orderbook;

import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import codingblackfemales.orderbook.visitor.OrderBookVisitor;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;

public class AskBookSide extends OrderBookSide {

    public void onBookUpdate(BookUpdateDecoder bookUpdate) throws Exception {
        removeMarketDataOrders();
        addMarketDataOrders(bookUpdate);
    }

    public void onAskBook(AskBookUpdateDecoder askBook) throws Exception {
        removeMarketDataOrders();
        addMarketDataOrders(askBook);
    }

    private void removeMarketDataOrders(){

        var level = getFirstLevel();

        level.removeMarketDataOrder();

        while(level.next() != null){
            level = level.next();
            level.removeMarketDataOrder();
        }
    }

    private void addMarketDataOrders(AskBookUpdateDecoder askDecoder){
        for(AskBookUpdateDecoder.AskBookDecoder decoder : askDecoder.askBook()) {
            final long price = decoder.price();
            final long quantity = decoder.size();
            var marketOrder = new MarketDataOrderFlyweight(price, quantity);
            addMarketDataOrder(marketOrder);
        }
    }

    private void addMarketDataOrder(MarketDataOrderFlyweight order){

    }

    private void addMarketDataOrders(BookUpdateDecoder bookUpdateDecoder){
        for(BookUpdateDecoder.AskBookDecoder decoder : bookUpdateDecoder.askBook()) {
            final long price = decoder.price();
            final long quantity = decoder.size();
            var marketOrder = new MarketDataOrderFlyweight(price, quantity);
            addMarketDataOrder(marketOrder);
        }
    }

    public void accept(final OrderBookVisitor visitor){
        final var firstLevel = getFirstLevel();
        visitor.visit(firstLevel);
    }
}

package codingblackfemales.orderbook;

import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import codingblackfemales.orderbook.visitor.MutatingAddOrderVisitor;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AskBookSide extends OrderBookSide {

    private static final Logger logger = LoggerFactory.getLogger(AskBookSide.class);

    private final MutatingAddOrderVisitor addOrderVisitor = new MutatingAddOrderVisitor();

    public void onBookUpdate(BookUpdateDecoder bookUpdate){
        removeMarketDataOrders();
        addAskMarketDataOrders(bookUpdate);
    }

    public void onAskBook(AskBookUpdateDecoder askBook){
        removeMarketDataOrders();
        addAskMarketDataOrders(askBook);
    }

    @Override
    MutatingAddOrderVisitor getAddOrderVisitor() {
        return addOrderVisitor;
    }

    boolean isBetweenLevels(OrderBookLevel previous, OrderBookLevel next, long price){
        return previous != null && next != null && previous.getPrice() < price && next.getPrice() > price;
    }

    boolean isNewDeepestLevel(OrderBookLevel previous, OrderBookLevel next, long price){
        return previous != null && next == null && previous.getPrice() < price;
    }

    public void addAskMarketDataOrders(AskBookUpdateDecoder askDecoder){
        for(AskBookUpdateDecoder.AskBookDecoder decoder : askDecoder.askBook()) {
            final long price = decoder.price();
            final long quantity = decoder.size();
            var marketOrder = new MarketDataOrderFlyweight(price, quantity);
            addMarketDataOrder(marketOrder);
        }
    }

    public void addAskMarketDataOrders(BookUpdateDecoder bookUpdateDecoder){
        for(BookUpdateDecoder.AskBookDecoder decoder : bookUpdateDecoder.askBook()) {
            final long price = decoder.price();
            final long quantity = decoder.size();
            logger.info("ASK: Adding order price:" + price + " quantity:" + quantity);
            var marketOrder = new MarketDataOrderFlyweight(price, quantity);
            addMarketDataOrder(marketOrder);
        }
    }



}

package codingblackfemales.orderbook;

import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import codingblackfemales.orderbook.visitor.MutatingAddOrderVisitor;
import codingblackfemales.orderbook.visitor.OrderBookVisitor;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BidBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BidBookSide extends OrderBookSide{

    private static final Logger logger = LoggerFactory.getLogger(BidBookSide.class);

    private final MutatingAddOrderVisitor addOrderVisitor = new MutatingAddOrderVisitor();

    public void onBidBook(BidBookUpdateDecoder bidBook) {
        removeMarketDataOrders();
        AddBidMarketDataOrders(bidBook);
    }

    public void onBookUpdate(BookUpdateDecoder bookUpdate) {
        removeMarketDataOrders();
        addBidkMarketDataOrders(bookUpdate);
    }

    @Override
    MutatingAddOrderVisitor getAddOrderVisitor() {
        return addOrderVisitor;
    }

    boolean isBetweenLevels(OrderBookLevel previous, OrderBookLevel next, long price){
        return previous != null && next != null && previous.getPrice() > price && next.getPrice() < price;
    }

    boolean isNewDeepestLevel(OrderBookLevel previous, OrderBookLevel next, long price){
        return previous != null && next == null && previous.getPrice() > price;
    }

    void AddBidMarketDataOrders(BidBookUpdateDecoder bidDecoder){
        for(BidBookUpdateDecoder.BidBookDecoder decoder : bidDecoder.bidBook()) {
            final long price = decoder.price();
            final long quantity = decoder.size();
            var marketOrder = new MarketDataOrderFlyweight(price, quantity);
            logger.info("[ORDERBOOK] BID: Adding order " + marketOrder);
            addMarketDataOrder(marketOrder);
        }
    }

    private void addBidkMarketDataOrders(BookUpdateDecoder bookUpdateDecoder){
        for(BookUpdateDecoder.AskBookDecoder decoder : bookUpdateDecoder.askBook()) {
            final long price = decoder.price();
            final long quantity = decoder.size();
            var marketOrder = new MarketDataOrderFlyweight(price, quantity);
            logger.info("[ORDERBOOK] BID: Adding order " + marketOrder);
            addMarketDataOrder(marketOrder);
        }
    }


}

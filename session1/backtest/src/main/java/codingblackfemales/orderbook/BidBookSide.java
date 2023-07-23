package codingblackfemales.orderbook;

import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import codingblackfemales.orderbook.visitor.MutatingAddOrderVisitor;
import codingblackfemales.orderbook.visitor.OrderBookVisitor;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BidBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;

public class BidBookSide extends OrderBookSide{

    private final MutatingAddOrderVisitor addOrderVisitor = new MutatingAddOrderVisitor();

    public void onBidBook(BidBookUpdateDecoder bidBook) throws Exception {
        removeMarketDataOrders();
        AddBidMarketDataOrders(bidBook);
    }

    public void onBookUpdate(BookUpdateDecoder bookUpdate) throws Exception {
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
            addMarketDataOrder(marketOrder);
        }
    }

    private void addBidkMarketDataOrders(BookUpdateDecoder bookUpdateDecoder){
        for(BookUpdateDecoder.AskBookDecoder decoder : bookUpdateDecoder.askBook()) {
            final long price = decoder.price();
            final long quantity = decoder.size();
            System.out.println("Adding order price:" + price + " quantity:" + quantity);
            var marketOrder = new MarketDataOrderFlyweight(price, quantity);
            addMarketDataOrder(marketOrder);
        }
    }


}

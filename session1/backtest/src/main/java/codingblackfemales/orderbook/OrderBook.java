package codingblackfemales.orderbook;

import codingblackfemales.orderbook.order.OrderFlyweight;
import codingblackfemales.orderbook.visitor.OrderBookVisitor;
import codingblackfemales.sequencer.event.MarketDataEventListener;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BidBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;

public class OrderBook extends MarketDataEventListener {

    public AskBookSide askBookSide = new AskBookSide();
    public BidBookSide bidBookSide = new BidBookSide();

    public AskBookSide getAskBookSide() {
        return askBookSide;
    }

    public BidBookSide getBidBookSide() {
        return bidBookSide;
    }

    public boolean canMatch(final OrderBookSide side, final long price, final long quantity){
        return false;
    }

    public void addOrder(final OrderFlyweight orderFlyweight){

    }

    @Override
    public void onBookUpdate(BookUpdateDecoder bookUpdate) throws Exception {
        getAskBookSide().onBookUpdate(bookUpdate);
        getBidBookSide().onBookUpdate(bookUpdate);
    }

    @Override
    public void onAskBook(AskBookUpdateDecoder askBook) throws Exception {
        getAskBookSide().onAskBook(askBook);
    }

    @Override
    public void onBidBook(BidBookUpdateDecoder bidBook) throws Exception {
        getBidBookSide().onBidBook(bidBook);
    }

}

package codingblackfemales.orderbook;

import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.sequencer.event.MarketDataEventListener;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BidBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;
import messages.order.Side;

public class OrderBook extends MarketDataEventListener {

    public AskBookSide askBookSide = new AskBookSide();
    public BidBookSide bidBookSide = new BidBookSide();

    public AskBookSide getAskBookSide() {
        return askBookSide;
    }

    public BidBookSide getBidBookSide() {
        return bidBookSide;
    }

    public boolean canMatch(final Side side, final long price){
        boolean canMatch = false;

        if(side.equals(Side.BUY)){
            canMatch = this.getAskBookSide().getFirstLevel().getPrice() <= price;
        }else if(side.equals(Side.SELL)){
            canMatch = this.getBidBookSide().getFirstLevel().getPrice() >= price;
        }

        return canMatch;
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

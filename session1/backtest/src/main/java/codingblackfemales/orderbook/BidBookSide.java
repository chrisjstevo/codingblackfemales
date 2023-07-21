package codingblackfemales.orderbook;

import codingblackfemales.orderbook.visitor.OrderBookVisitor;
import messages.marketdata.BidBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;

public class BidBookSide extends OrderBookSide{

    public void onBidBook(BidBookUpdateDecoder bidBook) throws Exception {

    }

    public void onBookUpdate(BookUpdateDecoder bookUpdate) throws Exception {

    }

    public void accept(final OrderBookVisitor visitor){

    }

}

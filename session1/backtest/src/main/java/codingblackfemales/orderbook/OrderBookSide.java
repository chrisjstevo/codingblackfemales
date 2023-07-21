package codingblackfemales.orderbook;

import codingblackfemales.sequencer.Sequencer;
import messages.marketdata.BookUpdateDecoder;

abstract class OrderBookSide {
    private OrderBookLevel firstLevel;

    public boolean canMatch(OrderBookSide side, long quantity, long price){
        return false;
    }

    public OrderBookLevel getFirstLevel() {
        return firstLevel;
    }
}

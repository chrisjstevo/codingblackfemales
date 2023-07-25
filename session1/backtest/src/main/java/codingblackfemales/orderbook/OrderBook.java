package codingblackfemales.orderbook;

import codingblackfemales.orderbook.channel.MarketDataChannel;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.orderbook.visitor.ReadOnlyMarketDataChannelPublishVisitor;
import codingblackfemales.sequencer.event.MarketDataEventListener;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BidBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;
import messages.order.Side;
import org.agrona.MutableDirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBook extends MarketDataEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderBook.class);

    private final MarketDataChannel marketDataChannel;

    public OrderBook(MarketDataChannel marketDataChannel) {
        this.marketDataChannel = marketDataChannel;
    }

    private ReadOnlyMarketDataChannelPublishVisitor mktDataVisitor = new ReadOnlyMarketDataChannelPublishVisitor();

    private AskBookSide askBookSide = new AskBookSide();
    private BidBookSide bidBookSide = new BidBookSide();

    public AskBookSide getAskBookSide() {
        return askBookSide;
    }

    public BidBookSide getBidBookSide() {
        return bidBookSide;
    }

    public boolean canMatch(final Side side, final long price){
        boolean canMatch = false;

        if(side.equals(Side.BUY) && this.getAskBookSide().getFirstLevel() != null){
            canMatch = this.getAskBookSide().getFirstLevel().getPrice() <= price;
        }else if(side.equals(Side.SELL) && this.getBidBookSide().getFirstLevel() != null){
            canMatch = this.getBidBookSide().getFirstLevel().getPrice() >= price;
        }

        return canMatch;
    }

    @Override
    public void onBookUpdate(BookUpdateDecoder bookUpdate) {
        getBidBookSide().onBookUpdate(bookUpdate);
        getAskBookSide().onBookUpdate(bookUpdate);
    }

    @Override
    public void onAskBook(AskBookUpdateDecoder askBook) {
        getAskBookSide().onAskBook(askBook);
    }

    @Override
    public void onBidBook(BidBookUpdateDecoder bidBook) {
        getBidBookSide().onBidBook(bidBook);
    }

    public void matchOrder(final LimitOrderFlyweight limit) {

    }

    public void addLiquidity(final LimitOrderFlyweight limit) {
        if(limit.getSide().equals(Side.BUY)){
            logger.info("Adding limit order to bid book" + limit);
            this.getBidBookSide().addLimitOrder(limit);
        }else{
            logger.info("Adding limit order to ask book" + limit);
            this.getAskBookSide().addLimitOrder(limit);
        }
    }

    public void onLimitOrder(final LimitOrderFlyweight limit) {
        if(canMatch(limit.getSide(), limit.getPrice())){
            matchOrder(limit);
        }else{
            addLiquidity(limit);
        }

        publishBook();
    }

    public void publishBook(){
        final var messageBuffer = getBookUpdateMessage();
        marketDataChannel.publish(messageBuffer);
    }

    public MutableDirectBuffer getBookUpdateMessage(){
        mktDataVisitor.start();
        getBidBookSide().accept(mktDataVisitor);
        getAskBookSide().accept(mktDataVisitor);
        return mktDataVisitor.end();
    }
}

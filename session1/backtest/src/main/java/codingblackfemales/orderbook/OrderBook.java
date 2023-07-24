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

public class OrderBook extends MarketDataEventListener {

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

    public void matchOrder(final LimitOrderFlyweight limit) {

    }

    public void addLiquidity(final LimitOrderFlyweight limit) {
        if(limit.getSide().equals(Side.BUY)){
            this.getBidBookSide().addLimitOrder(limit);
        }else{
            this.getAskBookSide().addLimitOrder(limit);
        }
    }

    public void onLimitOrder(final LimitOrderFlyweight limit) {
        if(canMatch(limit.getSide(), limit.getPrice())){
            matchOrder(limit);
        }else{
            addLiquidity(limit);
        }

        //publishBook();
    }

    public void publishBook(){
        final var messageBuffer = getBookUpdateMessage();
        //sendIt....
    }

    public MutableDirectBuffer getBookUpdateMessage(){
        mktDataVisitor.start();
        getBidBookSide().accept(mktDataVisitor);
        getAskBookSide().accept(mktDataVisitor);
        return mktDataVisitor.end();
    }
}

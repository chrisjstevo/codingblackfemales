package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.AskBookSide;
import codingblackfemales.orderbook.BidBookSide;
import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import messages.marketdata.*;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class ReadOnlyMarketDataChannelPublishVisitor implements OrderBookVisitor {

    private static final Logger logger = LoggerFactory.getLogger(ReadOnlyMarketDataChannelPublishVisitor.class);

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder encoder = new BookUpdateEncoder();

    private ByteBuffer byteBuffer; //= ByteBuffer.allocateDirect(1024);
    private UnsafeBuffer directBuffer; //= new UnsafeBuffer(byteBuffer);

    public void start(){
        byteBuffer = ByteBuffer.allocateDirect(1024);
        directBuffer = new UnsafeBuffer(byteBuffer);

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        //set the fields to desired valus
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);
        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.ORDERBOOK);
    }

    public MutableDirectBuffer end(){
        return directBuffer;
    }


    @Override
    public void visitLevel(OrderBookSide side, OrderBookLevel level) {}

    @Override
    public void visitSide(OrderBookSide side) {
        if(side instanceof BidBookSide){
            if(side.getFirstLevel() == null){
                return;
            }

            final var size = side.getFirstLevel().size();
            logger.debug("Bid Side Size: " + size);
            var bidBookEncoder = encoder.bidBookCount(size);
            OrderBookLevel level = side.getFirstLevel();
            for(int i=0; i< size; i++){
                logger.debug("Adding Mkt Data Msg BID: Price=" + level.getPrice() + " Qty=" + level.getQuantity());
                bidBookEncoder.next().size(level.getQuantity()).price(level.getPrice()) ;
                level = level.next();
            }
        }else if(side instanceof AskBookSide){
            if(side.getFirstLevel() == null){
                return;
            }
            final var size = side.getFirstLevel().size();
            logger.debug("Ask Side Size: " + size);
            var askBookEncoder = encoder.askBookCount(size);
            OrderBookLevel level = side.getFirstLevel();

            for(int i=0; i< size; i++){
                logger.debug("Adding Mkt Data Msg ASK: Price=" + level.getPrice() + " Qty=" + level.getQuantity());
                askBookEncoder.next().size(level.getQuantity()).price(level.getPrice()) ;
                level = level.next();
            }
        }
    }

    @Override
    public void visitOrder(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast) {}

    @Override
    public OrderBookLevel missingBookLevel(OrderBookLevel previous, OrderBookLevel next, long price) {
        return null;
    }

    @Override
    public OrderBookLevel onNoFirstLevel() {
        return null;
    }

    @Override
    public DefaultOrderFlyweight onNoFirstOrder() {
        return null;
    }
}

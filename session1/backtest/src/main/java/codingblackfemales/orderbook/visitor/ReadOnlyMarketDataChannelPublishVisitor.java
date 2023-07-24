package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.AskBookSide;
import codingblackfemales.orderbook.BidBookSide;
import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import messages.marketdata.BookUpdateEncoder;
import messages.marketdata.MessageHeaderEncoder;
import messages.marketdata.Venue;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class ReadOnlyMarketDataChannelPublishVisitor implements OrderBookVisitor {

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder encoder = new BookUpdateEncoder();

    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    private final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

    public void start(){
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        //set the fields to desired valus
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);
    }

    public MutableDirectBuffer end(){
        return directBuffer;
    }


    @Override
    public void visit(OrderBookSide side, OrderBookLevel level) {}

    @Override
    public void visit(OrderBookSide side) {
        if(side instanceof BidBookSide){
            var bidBookEncoder = encoder.bidBookCount(side.getFirstLevel().size());
            OrderBookLevel level = side.getFirstLevel();
            while(level != null){
                bidBookEncoder.next().size(level.getQuantity()).price(level.getPrice()) ;
            }
        }else if(side instanceof AskBookSide){
            var askBookEncoder = encoder.askBookCount(side.getFirstLevel().size());
            OrderBookLevel level = side.getFirstLevel();
            while(level != null){
                askBookEncoder.next().size(level.getQuantity()).price(level.getPrice()) ;
            }
        }
    }

    @Override
    public void visit(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast) {}

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

package codingblackfemales.orderbook;

import codingblackfemales.orderbook.channel.MarketDataChannel;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import messages.marketdata.*;
import messages.order.Side;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.ByteBuffer;

import static codingblackfemales.orderbook.MatchingOrderBookVisitorTest.wrapBufferInDecoder;

public class CancelOrderBookVisitorTest {

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder bookUpdateEncoder = new BookUpdateEncoder();

    private UnsafeBuffer tick1(){
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        bookUpdateEncoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        //set the fields to desired values
        bookUpdateEncoder.venue(Venue.XLON);
        bookUpdateEncoder.instrumentId(123L);
        bookUpdateEncoder.source(Source.STREAM);

        bookUpdateEncoder.bidBookCount(3)
                .next().price(100L).size(100L)
                .next().price(96L).size(200L)
                .next().price(93L).size(300L);

        bookUpdateEncoder.askBookCount(4)
                .next().price(101L).size(101L)
                .next().price(115L).size(200L)
                .next().price(120L).size(5000L)
                .next().price(130L).size(5000L);

        bookUpdateEncoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        return directBuffer;
    }

    @Test
    public void testCancelBuy(){

        final MarketDataChannel mktDataChannel = Mockito.mock(MarketDataChannel.class);
        final OrderChannel orderChannel = Mockito.mock(OrderChannel.class);

        final OrderBook book = new OrderBook(mktDataChannel, orderChannel);

        final var buffer = tick1();

        final var bookUpdateDecoder = wrapBufferInDecoder(buffer);

        book.onBookUpdate(bookUpdateDecoder);

        Assert.assertEquals( 200L, book.getBidBookSide().getFirstLevel().next().getQuantity());

        book.onLimitOrder(new LimitOrderFlyweight(Side.BUY, 96L, 500L, 1));

        Assert.assertEquals( 700L, book.getBidBookSide().getFirstLevel().next().getQuantity());

        book.onCancelOrder(1);

        Assert.assertEquals( 200L, book.getBidBookSide().getFirstLevel().next().getQuantity());
    }

    @Test
    public void testCancelSell(){

        final MarketDataChannel mktDataChannel = Mockito.mock(MarketDataChannel.class);
        final OrderChannel orderChannel = Mockito.mock(OrderChannel.class);

        final OrderBook book = new OrderBook(mktDataChannel, orderChannel);

        final var buffer = tick1();

        final var bookUpdateDecoder = wrapBufferInDecoder(buffer);

        book.onBookUpdate(bookUpdateDecoder);

        Assert.assertEquals( 5_000L, book.getAskBookSide().getFirstLevel().next().next().getQuantity());

        book.onLimitOrder(new LimitOrderFlyweight(Side.SELL, 120L, 1_000L, 1));

        Assert.assertEquals( 6_000L, book.getAskBookSide().getFirstLevel().next().next().getQuantity());

        book.onCancelOrder(1);

        Assert.assertEquals( 5_000L, book.getAskBookSide().getFirstLevel().next().next().getQuantity());
    }



}

package codingblackfemales.orderbook;

import codingblackfemales.orderbook.channel.MarketDataChannel;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import messages.marketdata.*;
import messages.order.Side;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MatchingOrderBookVisitorTest {

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final BookUpdateDecoder bookUpdateDecoder = new BookUpdateDecoder();

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder bookUpdateEncoder = new BookUpdateEncoder();

    private UnsafeBuffer createBookUpdateMessageTick2(){
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
    public void testMatchingMarketDataOrdersWithLimit(){

        final MarketDataChannel mktDataChannel = Mockito.mock(MarketDataChannel.class);
        final OrderChannel orderChannel = Mockito.mock(OrderChannel.class);

        final OrderBook book = new OrderBook(mktDataChannel, orderChannel);

        final var buffer = createBookUpdateMessageTick2();

        headerDecoder.wrap(buffer, 0);

        final int actingBlockLength = headerDecoder.blockLength();
        final int actingVersion = headerDecoder.version();
        final int bufferOffset = headerDecoder.encodedLength();

        bookUpdateDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);

        book.onBookUpdate(bookUpdateDecoder);

        book.onLimitOrder(new LimitOrderFlyweight(Side.BUY, 101L, 100L, 1));

        verify(orderChannel, times(1)).publishFill(eq(100l), any());
    }

    @Test
    public void testMatchingMoreThanOneLevelMarketDataOrdersWithLimit(){

        final MarketDataChannel mktDataChannel = Mockito.mock(MarketDataChannel.class);
        final OrderChannel orderChannel = Mockito.mock(OrderChannel.class);

        final OrderBook book = new OrderBook(mktDataChannel, orderChannel);

        final var buffer = createBookUpdateMessageTick2();

        headerDecoder.wrap(buffer, 0);

        final int actingBlockLength = headerDecoder.blockLength();
        final int actingVersion = headerDecoder.version();
        final int bufferOffset = headerDecoder.encodedLength();

        bookUpdateDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);

        book.onBookUpdate(bookUpdateDecoder);

        book.onLimitOrder(new LimitOrderFlyweight(Side.BUY, 115L, 150L, 1));

        verify(orderChannel, times(1)).publishFill(eq(101L), any());
        verify(orderChannel, times(1)).publishFill(eq(49L), any());
    }

    @Test
    public void testSellMatchingMoreThanOneLevelMarketDataOrdersWithLimit(){

        final MarketDataChannel mktDataChannel = Mockito.mock(MarketDataChannel.class);
        final OrderChannel orderChannel = Mockito.mock(OrderChannel.class);

        final OrderBook book = new OrderBook(mktDataChannel, orderChannel);

        final var buffer = createBookUpdateMessageTick2();

        headerDecoder.wrap(buffer, 0);

        final int actingBlockLength = headerDecoder.blockLength();
        final int actingVersion = headerDecoder.version();
        final int bufferOffset = headerDecoder.encodedLength();

        bookUpdateDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);

        book.onBookUpdate(bookUpdateDecoder);

        book.onLimitOrder(new LimitOrderFlyweight(Side.SELL, 96L, 280L, 1));

        verify(orderChannel, times(1)).publishFill(eq(100L), any());
        verify(orderChannel, times(1)).publishFill(eq(180L), any());
    }

}

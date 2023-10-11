package codingblackfemales.orderbook;

import codingblackfemales.orderbook.channel.MarketDataChannel;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Ignore;
import org.mockito.Mockito;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class OrderBookTest {

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final BookUpdateDecoder bookUpdateDecoder = new BookUpdateDecoder();

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder bookUpdateEncoder = new BookUpdateEncoder();

    private UnsafeBuffer createBookUpdateMessageTick1(){
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        bookUpdateEncoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        //set the fields to desired values
        bookUpdateEncoder.venue(Venue.XLON);
        bookUpdateEncoder.instrumentId(123L);
        bookUpdateEncoder.source(Source.STREAM);

        bookUpdateEncoder.askBookCount(3)
                .next().price(100L).size(101L)
                .next().price(110L).size(200L)
                .next().price(115L).size(5000L);

        bookUpdateEncoder.bidBookCount(3)
                .next().price(98L).size(100L)
                .next().price(95L).size(200L)
                .next().price(91L).size(300L);

        bookUpdateEncoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        return directBuffer;
    }

    private UnsafeBuffer createBookUpdateMessageTick2(){
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        bookUpdateEncoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        //set the fields to desired values
        bookUpdateEncoder.venue(Venue.XLON);
        bookUpdateEncoder.instrumentId(123L);
        bookUpdateEncoder.source(Source.STREAM);

        bookUpdateEncoder.askBookCount(4)
                .next().price(101L).size(101L)
                .next().price(115L).size(200L)
                .next().price(120L).size(5000L)
                .next().price(125L).size(9000L);

        bookUpdateEncoder.bidBookCount(3)
                .next().price(100L).size(100L)
                .next().price(96L).size(200L)
                .next().price(93L).size(300L);



        bookUpdateEncoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        return directBuffer;
    }



    @Ignore
    public void testOrderBookFunctionality() throws Exception{

        final var buffer = createBookUpdateMessageTick1();

        headerDecoder.wrap(buffer, 0);

        final int actingBlockLength = headerDecoder.blockLength();
        final int actingVersion = headerDecoder.version();
        final int bufferOffset = headerDecoder.encodedLength();

        bookUpdateDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);

        final MarketDataChannel channel = Mockito.mock(MarketDataChannel.class);
        final OrderChannel orderChannel = Mockito.mock(OrderChannel.class);

        final OrderBook orderBook = new OrderBook(channel, orderChannel);

        orderBook.onBookUpdate(bookUpdateDecoder);

        assertEquals(100L, orderBook.getAskBookSide().getFirstLevel().getPrice());
        assertEquals(110L, orderBook.getAskBookSide().getFirstLevel().next().getPrice());
        assertEquals(115L, orderBook.getAskBookSide().getFirstLevel().next().next().getPrice());

        assertEquals(101L, orderBook.getAskBookSide().getFirstLevel().getQuantity());
        assertEquals(200L, orderBook.getAskBookSide().getFirstLevel().next().getQuantity());
        assertEquals(5000L, orderBook.getAskBookSide().getFirstLevel().next().next().getQuantity());

        assertNull(orderBook.getAskBookSide().getFirstLevel().next().next().next());

        assertEquals(98L, orderBook.getBidBookSide().getFirstLevel().getPrice());
        assertEquals(95L, orderBook.getBidBookSide().getFirstLevel().next().getPrice());
        assertEquals(91L, orderBook.getBidBookSide().getFirstLevel().next().next().getPrice());

        assertEquals(100L, orderBook.getBidBookSide().getFirstLevel().getQuantity());
        assertEquals(200L, orderBook.getBidBookSide().getFirstLevel().next().getQuantity());
        assertEquals(300L, orderBook.getBidBookSide().getFirstLevel().next().next().getQuantity());


        assertTrue(orderBook.getAskBookSide().getFirstLevel().getFirstOrder() instanceof MarketDataOrderFlyweight);

        final var buffer2 = createBookUpdateMessageTick2();

        headerDecoder.wrap(buffer2, 0);

        final int actingBlockLength2 = headerDecoder.blockLength();
        final int actingVersion2 = headerDecoder.version();
        final int bufferOffset2 = headerDecoder.encodedLength();

        bookUpdateDecoder.wrap(buffer2, bufferOffset2, actingBlockLength2, actingVersion2);

        orderBook.onBookUpdate(bookUpdateDecoder);

        assertEquals(101L, orderBook.getAskBookSide().getFirstLevel().getPrice());
        assertEquals(115L, orderBook.getAskBookSide().getFirstLevel().next().getPrice());
        assertEquals(120L, orderBook.getAskBookSide().getFirstLevel().next().next().getPrice());

        assertEquals(101L, orderBook.getAskBookSide().getFirstLevel().getQuantity());
        assertEquals(200L, orderBook.getAskBookSide().getFirstLevel().next().getQuantity());
        assertEquals(5000L, orderBook.getAskBookSide().getFirstLevel().next().next().getQuantity());

        assertNull(orderBook.getAskBookSide().getFirstLevel().next().next().next());

        assertEquals(100L, orderBook.getBidBookSide().getFirstLevel().getPrice());
        assertEquals(96L, orderBook.getBidBookSide().getFirstLevel().next().getPrice());
        assertEquals(93L, orderBook.getBidBookSide().getFirstLevel().next().next().getPrice());

        assertEquals(100L, orderBook.getBidBookSide().getFirstLevel().getQuantity());
        assertEquals(200L, orderBook.getBidBookSide().getFirstLevel().next().getQuantity());
        assertEquals(300L, orderBook.getBidBookSide().getFirstLevel().next().next().getQuantity());

    }
}

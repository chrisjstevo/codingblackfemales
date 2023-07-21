package codingblackfemales.orderbook;

import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

public class OrderBookTest {

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final BookUpdateDecoder bookUpdateDecoder = new BookUpdateDecoder();

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder bookUpdateEncoder = new BookUpdateEncoder();

    private UnsafeBuffer createBookUpdateMessage(){
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        bookUpdateEncoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        //set the fields to desired values
        bookUpdateEncoder.venue(Venue.XLON);
        bookUpdateEncoder.instrumentId(123L);

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

    @Test
    public void testOrderBookFunctionality() throws Exception{

        final var buffer = createBookUpdateMessage();

        headerDecoder.wrap(buffer, 0);

        final int actingBlockLength = headerDecoder.blockLength();
        final int actingVersion = headerDecoder.version();
        final int bufferOffset = headerDecoder.encodedLength();

        bookUpdateDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);

        final OrderBook orderBook = new OrderBook();

        //TODO: CJS
        //orderBook.onBookUpdate(bookUpdateDecoder);

    }
}

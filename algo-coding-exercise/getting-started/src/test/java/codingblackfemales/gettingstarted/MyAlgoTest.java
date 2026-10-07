package codingblackfemales.gettingstarted;
import codingblackfemales.algo.AlgoLogic;
import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import java.nio.ByteBuffer;
import static org.junit.Assert.assertEquals;

/**
 * This test is designed to check your algo behavior in isolation of the order book.
 *
 * You can tick in market data messages by creating new versions of createTick() (ex. createTick2, createTickMore etc..)
 *
 * You should then add behaviour to your algo to respond to that market data by creating or cancelling child orders.
 *
 * When you are comfortable you algo does what you expect, then you can move on to creating the MyAlgoBackTest.
 *
 */
public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    protected UnsafeBuffer createTickStart() {

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
                .next().price(98L).size(100L)
                .next().price(95L).size(200L)
                .next().price(91L).size(300L);

        encoder.askBookCount(3)
                .next().price(100L).size(101L)
                .next().price(110L).size(200L)
                .next().price(115L).size(5000L);


        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    protected UnsafeBuffer createTickPriceSame() {

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
                .next().price(98L).size(100L)
                .next().price(95L).size(200L)
                .next().price(91L).size(300L);

        encoder.askBookCount(3)
                .next().price(100L).size(101L)
                .next().price(110L).size(200L)
                .next().price(115L).size(5000L);


        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    protected UnsafeBuffer createTickPriceUp() {

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
                .next().price(99L).size(100L)
                .next().price(95L).size(200L)
                .next().price(91L).size(300L);

        encoder.askBookCount(3)
                .next().price(100L).size(101L)
                .next().price(110L).size(200L)
                .next().price(115L).size(5000L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    protected UnsafeBuffer createTickWithBestBid(long bestBid) {
        //                                       │
        //               the input: a box called bestBid, holding a long

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
                .next().price(bestBid).size(100L)        // best bid = whatever you passed in
                .next().price(bestBid - 3).size(200L)    // a bit lower
                .next().price(bestBid - 7).size(300L);   // lower again

        encoder.askBookCount(3)
                .next().price(bestBid + 2).size(101L)    // best ask always 2 above the best bid
                .next().price(bestBid + 12).size(200L)
                .next().price(bestBid + 17).size(5000L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

// ---------- tests ----------

    @Test
    public void testCreatesOrderWhenNoLiveOrder() throws Exception {
        send(createTickStart());                                        // given + when
        assertEquals(1, container.getState().getChildOrders().size());  // then: 1 order made
    }

    @Test
    public void testWaitsWhenStillAtBestBid() throws Exception {
        send(createTickStart());                                        // given
        send(createTickPriceSame());                                    // when
        assertEquals(1, container.getState().getChildOrders().size());  // then: still only 1 order
    }

    @Test
    public void testCancelsWhenBestBidMovesUp() throws Exception {
        send(createTickStart());                                        // given
        send(createTickPriceUp());                                      // when
        assertEquals(2, container.getState().getChildOrders().size());  // then: old one cancelled, new one made
        assertEquals(1, container.getState().getActiveChildOrders().size()); // and only 1 is live
    }

    @Test
    public void testStopsAfterFiveOrders() throws Exception {
        send(createTickWithBestBid(98));    // given: the starting market
        send(createTickWithBestBid(99));    // when: the price keeps rising...
        send(createTickWithBestBid(100));
        send(createTickWithBestBid(101));
        send(createTickWithBestBid(102));
        send(createTickWithBestBid(103));   // ...6th order made
        send(createTickWithBestBid(104));   // one more move, to prove it stays stopped

        assertEquals(6, container.getState().getChildOrders().size());
    }
}


package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;


/**
 * This test is designed to check your algo behavior in isolation of the order book.
 * You can tick in market data messages by creating new versions of createTick() (ex. createTick2, createTickMore etc..)
 * You should then add behaviour to your algo to respond to that market data by creating or cancelling child orders.
 * When you are comfortable you algo does what you expect, then you can move on to creating the MyAlgoBackTest.
 */
public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        //this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }

    @Test
    public void testDispatchThroughSequencer() throws Exception {

        //create a sample market data tick.... - no orders fulfilled
        //The bids are higher than the ask price
        send(createTick());
        assertEquals(container.getState().getChildOrders().size(), 5);
        assertEquals(container.getState().getActiveChildOrders().size(), 4);
        assertEquals(container.getState().getChildOrders().stream().filter(co -> co.getFilledQuantity() == 0).count(), 5);

        //The bids are lower than ask price
        send(createTick2());
        assertEquals(container.getState().getChildOrders().size(), 5);
        assertEquals(container.getState().getActiveChildOrders().size(), 4);
        assertEquals(container.getState().getChildOrders().stream().filter(co -> co.getFilledQuantity() == 0).count(), 5);

    }

    protected UnsafeBuffer createTick2() {

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);


        encoder.bidBookCount(3)
                .next().price(98L).size(100L)
                .next().price(95L).size(200L)
                .next().price(91L).size(300L);

        encoder.askBookCount(3)
                .next().price(100L).size(101L)
                .next().price(110L).size(200L)
                .next().price(115L).size(5000L);;

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }
}

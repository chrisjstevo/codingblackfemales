package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;

import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * This test plugs together all the infrastructure, including the order book (which you can trade against)
 * and the market data feed.
 *
 * If your algo adds orders to the book, they will reflect in your market data coming back from the order book.
 *
 * If you cross the spread (i.e. you BUY an order with a price which is == or > askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the childOrders of the state object.
 *
 * If you cancel the order your child order will show the order status as cancelled in the childOrders of the state object.
 *
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {

        send(createTick());
        assertEquals(container.getState().getChildOrders().size(), 5);
        assertEquals(container.getState().getActiveChildOrders().size(), 4);

        send(createTick2());
        assertEquals(container.getState().getChildOrders().size(), 5);
        assertEquals(container.getState().getActiveChildOrders().size(), 4);
        assertEquals(container.getState().getChildOrders().stream().filter(cf -> cf.getFilledQuantity()>0).count(), 0);



    }


    protected UnsafeBuffer createTick3(){
        //market moving towards buyers - asks are lower than bids

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
                .next().price(104L).size(100L)
                .next().price(103L).size(200L)
                .next().price(102L).size(5000L);

        encoder.askBookCount(3)
                .next().price(100L).size(111L)
                .next().price(101L).size(222L)
                .next().price(102L).size(3333L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;

    }
}
package codingblackfemales.gettingstarted;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import codingblackfemales.algo.AlgoLogic;
import messages.marketdata.BookUpdateEncoder;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.MessageHeaderEncoder;
import messages.marketdata.Source;
import messages.marketdata.Venue;
import messages.order.Side;

public class MyAlgoStretchTest extends AbstractAlgoTest {

    @Override 
    public AlgoLogic createAlgoLogic() {
        // Creates the MyAlgoLogic instance used by the test container
        // true enables the stretch-goal algorithm
        return new MyAlgoLogic(true);
    }

    // STRETCH GOAL
    // Creates a market-data tick with a configurable ask price
    // The ask price is passed into the method so the stretch-goal test can simulate different market observations:
    // 100 -> 98 -> 97 -> 93
    // The bid side stays the same because the stretch test is focused
    // on changes to the ask price
    protected UnsafeBuffer createStretchTick(long askPrice) {
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
    
            .next().price(95L).size(100L)
            .next().price(93L).size(200L)
            .next().price(91L).size(300L);
        
             
        encoder.askBookCount(1)
          // Use the price supplied by the test as the current best ask
          .next().price(askPrice).size(100L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

     @Test
    public void testStretchGoalBuildsReferenceBeforeBuying() throws Exception {

    // The stretch goal uses several market observations to build a reference price before looking for a cheaper buying opportunity

    // The overall goal is to BUY when the market is sufficiently below the reference price
    // and SELL when it is sufficiently above it
    
    // First observation: best ask = 100
    send(createStretchTick(100));

    // Second observation: ask = 98
    send(createStretchTick(98));

    // Third observation: ask = 97
    send(createStretchTick(97));

    // Reference = (100 + 98 + 97) / 3 = 98
    // 5% discount threshold = 98 * 95 / 100 = 93

    // Therefore, the algo should consider buying when the ask reaches 93 or lower

    // Fourth observation: ask = 93
    // This should trigger the BUY.
    send(createStretchTick(93));

    var childOrder = container.getState()
            .getActiveChildOrders()
            .get(0);

    // Check that the stretch algo created a BUY at the expected price
    assertEquals(Side.BUY, childOrder.getSide());
    assertEquals(93, childOrder.getPrice());
    }

    @Test 
    public  void testStretchGoalBuysAndThenSells() throws Exception {

        // Build the reference price from three market observations:
        // 100, 98 and 97 gives a reference price of 98
        send(createStretchTick(100));
        send(createStretchTick(98));
        send(createStretchTick(97));

        // The 5% discount threshold is 93, so this should trigger the BUY
        send(createStretchTick(93));

        var buyOrder = container.getState().getActiveChildOrders().get(0);

       // Check that the algo created the expected BUY order
       assertEquals(Side.BUY, buyOrder.getSide());
       assertEquals(93, buyOrder.getPrice()); 

        // The 5% premium threshold is 102, so this should trigger the SELL
        send(createStretchTick(102));

        var childOrder = container.getState().getChildOrders();
        // The algo should now have both the original BUY and the new SELL order
        assertEquals(2, childOrder.size());

        var sellOrder = childOrder.get(1);

        // Check that the algo created the expected SELL order
        assertEquals(Side.SELL, sellOrder.getSide());
       assertEquals(102, sellOrder.getPrice());
    }
    
}

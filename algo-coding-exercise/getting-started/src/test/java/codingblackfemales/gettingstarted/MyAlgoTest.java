package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import messages.marketdata.BookUpdateEncoder;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.MessageHeaderEncoder;
import messages.marketdata.Source;
import messages.marketdata.Venue;
import messages.order.Side;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;


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
        // Creates the MyAlgoLogic instance used by the test container
        // The default constructor runs the main algorithm
        return new MyAlgoLogic();
    }

    // Creates a second market-data scenario where the best bid changes from 98 to 95
    // This is used to test whether the algo cancels the existing order and creates a new order at the updated best bid
    protected UnsafeBuffer createBestBidChangedTick() {
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
                 // New best bid: 95
                .next().price(95L).size(100L)
                .next().price(93L).size(200L)
                .next().price(91L).size(300L);

        encoder.askBookCount(4)
                .next().price(98L).size(501L)
                .next().price(101L).size(200L)
                .next().price(110L).size(5000L)
                .next().price(119L).size(5600L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    // Creates market data where the best bid has 500 shares available
    // This is used to test that the algo applies the maximum order quantity of 100 rather than creating an order for all 500 shares
    protected UnsafeBuffer createLargeQuantityTick() {
        
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
            // 500 shares are available, so the algo should limit the order to 100
            .next().price(98L).size(500L)
            .next().price(95L).size(200L)
            .next().price(91L).size(300L);

        encoder.askBookCount(4)
            .next().price(100L).size(101L)
            .next().price(110L).size(200L)
            .next().price(115L).size(5000L)
            .next().price(119L).size(5600L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    // Creates market data with no bids in the order book
    // This tests that the algo does not try to create a BUY order when there is no available bid price
    protected UnsafeBuffer createNoBidTick() {
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(0);
                
        encoder.askBookCount(4)
                .next().price(98L).size(501L)
                .next().price(101L).size(200L)
                .next().price(110L).size(5000L)
                .next().price(119L).size(5600L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    @Test
    public void testCreatesNewOrderWhenBestBidChanges() throws Exception {

        // Send the initial market-data tick

        // The best bid in createTick() is 98, so the algo should create a BUY order at 98 with a quantity of 100
        send(createTick());

        // Check that one child order was created
        assertEquals(1, container.getState().getChildOrders().size());

       
        var childOrder = container.getState().getActiveChildOrders().get(0);

        // Check the order side, price and quantity
        assertEquals(Side.BUY, childOrder.getSide());
        assertEquals(98, childOrder.getPrice());
        assertEquals(100, childOrder.getQuantity());

        // Send a second market-data tick where the best bid changes from 98 to 95
        send(createBestBidChangedTick());
        
        // There should still be only one active order after the replacement
        assertEquals(1, container.getState().getActiveChildOrders().size()); 

        var newChildOrder = container.getState().getActiveChildOrders().get(0);

        // Check that the replacement order uses the new best bid of 95
        assertEquals(Side.BUY, newChildOrder.getSide());
        assertEquals(95, newChildOrder.getPrice());
        assertEquals(100, newChildOrder.getQuantity());

    }

    @Test
    public void testMaximumOrderQuantity() throws Exception {

        // Send market data where 500 shares are available at the best bid
        // The algo has a maximum child-order quantity of 100, so it should only create an order for 100 shares
        send(createLargeQuantityTick());

        var childOrder = container.getState().getActiveChildOrders().get(0);

         // Check that the order uses the best bid price and is limited to 100 shares
        assertEquals(98, childOrder.getPrice());
        assertEquals(100, childOrder.getQuantity());
    }


    @Test
    public void testNoBidDoesNotCreateOrder() throws Exception {

        // Send market data with an empty bid side
        // Without a best bid price, the algo should take no action and should not create a child order
        send(createNoBidTick());

        assertEquals(0, container.getState().getChildOrders().size());
       
    }
}

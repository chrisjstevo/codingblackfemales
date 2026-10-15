package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
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
 * This test is designed to check your algo behavior in isolation of the order
 * book.
 *
 * You can tick in market data messages by creating new versions of createTick()
 * (ex. createTick2, createTickMore etc..)
 *
 * You should then add behaviour to your algo to respond to that market data by
 * creating or cancelling child orders.
 *
 * When you are comfortable you algo does what you expect, then you can move on
 * to creating your MyAlgoBackTest.
 *
 */

// tick = update or event in the market
public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        // This adds algo logic to the container classes
        return new MyAlgoLogic();
    }

    // @Test annotation tells JUnit to run it as a test
    // Java unit tests are written as a method
    // testDispatchThroughSequencer - method name

    // TEST 1
    @Test
    public void testDispatchThroughSequencer() throws Exception {

        // Send the first market-data tick into the trading infrastructure.
        // createTick() puts details such as the best bid price and quantity into buffer
        // container and decodes the message from the buffer
        send(createTick());

        // createTick() has a best bid of 98.
        // maximum buying price is also 98,
        // so the algo should create one buy order
        assertEquals(
                1,
                container.getState().getChildOrders().size());

        // Get the child order created by algo
        ChildOrder order = container.getState().getChildOrders().get(0);

        // Check that it is a BUY order
        assertEquals(Side.BUY, order.getSide());

        // Check that the order quantity is 10
        assertEquals(10, order.getQuantity());

        // Check that the order price is 98
        assertEquals(98, order.getPrice());

        // Send another market-data tick
        // In createTick2() best bid has increased to 100
        send(createTick2());

        // 100 is above maximum buying price of 98
        // so the algo should cancel the existing order
        assertEquals(
                OrderState.CANCELLED,
                container.getState().getChildOrders().get(0).getState());
    }

    // TEST 2
    @Test
    public void testDoesNotCreateDuplicateOrders() throws Exception {

        // Send the first market-data tick
        send(createTick());

        // best bid is 98 so algo should create one BUY order
        assertEquals(
                1,
                container.getState().getChildOrders().size());

        // Send another tick where the best bid is still 98.
        send(createTick3());

        // The existing order is still valid, so the algorithm should
        // not create another child order
        assertEquals(
                1,
                container.getState().getChildOrders().size());
    }

    // TEST 3
    @Test
    public void testDoesNotCreateOrderAboveLimit() throws Exception {

        // Send market data where the best bid is above maximum buying price
        send(createTick4());

        // The best bid is 100, which is above limit of 98
        // algo should not create a child order
        assertEquals(
                0,
                container.getState().getChildOrders().size());
    }

    // CODE ADDED AFTER JACOCO COVERAGE

    // sends a market-data tick that describes a book with one ask level and no bids
    // assertion checks that no child order was created

    @Test
    public void testDoesNothingWhenThereAreNoBids() throws Exception {

        // creates encoders to build a market-data update message
        MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        BookUpdateEncoder encoder = new BookUpdateEncoder();

        // creates a buffer to hold the encoded message
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        UnsafeBuffer buffer = new UnsafeBuffer(byteBuffer);

        encoder.wrapAndApplyHeader(buffer, 0, headerEncoder);
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        // Sets the tick to have no bid levels and one ask level
        // 101 units offered at price 100.
        encoder.bidBookCount(0);
        encoder.askBookCount(1)
                .next().price(100L).size(101L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        // sends the market-data message through the test system
        // algo receives it and sees there are no bids, and returns NoAction.
        send(buffer);

        // checks that no child order was created.
        assertEquals(0, container.getState().getChildOrders().size());
    }

    @Test
    public void testCreatesNewOrderAfterExistingOrderIsFilled() throws Exception {

        // send intiial market data tick
        send(createTick());

        // get the first ChildOrder state and store it in firstOrder
        ChildOrder firstOrder = container.getState().getChildOrders().get(0);

        // Set the order state to FILLED
        firstOrder.setState(OrderState.FILLED);

        // send another tick
        send(createTick3());

        // skips any filled orders and creates new order if best bid is still within
        // limit
        assertEquals(2, container.getState().getChildOrders().size());

        // get the second ChildOrder state and store it in newOrder
        ChildOrder newOrder = container.getState().getChildOrders().get(1);

        // assert new order is on BUY side for 10 @ 98
        assertEquals(Side.BUY, newOrder.getSide());
        assertEquals(10, newOrder.getQuantity());
        assertEquals(98, newOrder.getPrice());
    }
}
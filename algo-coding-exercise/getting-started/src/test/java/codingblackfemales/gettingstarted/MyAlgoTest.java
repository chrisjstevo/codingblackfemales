package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// import static org.junit.Assert.assertEquals;

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
        //this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }


    @Test
    public void testDispatchThroughSequencer() throws Exception {

        //create a sample market data tick....
        send(createTick());

        //simple assert to check we had 3 orders created
        assertEquals(3, container.getState().getChildOrders().size());
    }

    
    @Test
    public void shouldCreateThreeBuyOrdersWhenThereAreNoActiveOrders() throws Exception {
    
            send(createTick());

            assertEquals(3, container.getState().getChildOrders().size());
            assertEquals(3, container.getState().getActiveChildOrders().size());
    }


    @Test
    public void shouldNotCreateMoreThanThreeActiveOrders() throws Exception {
        send(createTick());

        assertEquals(3, container.getState().getChildOrders().size());
        assertEquals(3, container.getState().getActiveChildOrders().size());

        send(createTick());
       
        assertEquals(3, container.getState().getChildOrders().size());
        assertEquals(3, container.getState().getActiveChildOrders().size());
    }
    
    // test for doing nothing when the price is still valid    
    @Test
    public void testDoesNothingWhenBestBidHasNotChanged() throws Exception {

        send(createTick());

        assertEquals(3, container.getState().getActiveChildOrders().size());
        assertEquals(3, container.getState().getChildOrders().size());

        container.getState().getActiveChildOrders().forEach(order ->
                assertEquals(100L, order.getPrice())
        );

        send(createTick());

        // Nothing should have been cancelled or created.
        assertEquals(3, container.getState().getActiveChildOrders().size());
        assertEquals(3, container.getState().getChildOrders().size());

        // All active orders should still be at 100.
        container.getState().getActiveChildOrders().forEach(order ->
                assertEquals(100L, order.getPrice())
        );

    }

    @Test 
    public void testCancelsChildOrderWhenMarketPriceChanges() throws Exception {
        send(createTick());

        assertEquals(3, container.getState().getActiveChildOrders().size());

        send(createTick2());

        // assert that the an order was cancelled by checking that it is no longer actives instead
        assertTrue(container.getState().getChildOrders().size() > container.getState().getActiveChildOrders().size());

    }

    @Test 
    public void testAlgoRespondsToMultipleMarketDataUpdates() throws Exception {
        send(createTick());

        assertEquals(3, container.getState().getActiveChildOrders().size());

        send(createTick2());

        send(createTick3());

        assertTrue(container.getState().getChildOrders().size() >= 3);
    }

}

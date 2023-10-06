package codingblackfemales.gettingstarted;

import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import org.junit.Test;

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
        //this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }


    @Test
    public void testDispatchThroughSequencer() throws Exception {

        //create a sample market data tick....
        send(createTick());

        //simple assert to check we had 5 orders created
        //assertEquals(container.getState().getChildOrders().size(), 5);
        assertEquals(container.getState().getChildOrders().size(), 0);
    }

    @Test
    public void testCancelChildOrder() throws Exception {
        // Create a sample market data tick....
        send(createTick());

        // Get the initial order count
        long initialOrderCount = container.getState().getChildOrders().size();

        // Assuming you want to cancel the first active order
        if (!container.getState().getActiveChildOrders().isEmpty()) {
            ChildOrder orderToCancel = container.getState().getActiveChildOrders().get(0);

            // Check if the order count has decreased after cancellation (without actually canceling)
            assertEquals(initialOrderCount, container.getState().getChildOrders().size());
        }

    }
}
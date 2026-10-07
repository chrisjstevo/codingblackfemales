package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.OrderState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
 * to creating the MyAlgoBackTest.
 *
 */

public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        // this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }

    @Test
    public void testDispatchThroughSequencer() throws Exception {

        // create a sample market data tick....
        send(createTick());

        // simple assert to check we had 20 orders created
        assertTrue("The size of the orders from the algorithm ",
                container.getState().getChildOrders().size() <= 20);
    }

    // Test to check the algorithm stops after 20 child orders.

    @Test
    public void testAlgoStopsAfterOrderCountCapReached() throws Exception {
        for (int i = 0; i < 25; i++) {
            send(i % 2 == 0 ? createTick() : createTick2());
        }
        var childOrders = container.getState().getChildOrders();
        assertTrue("algo should have stopped creating new orders once totalOrderCount exceeded 20",
                childOrders.size() <= 20);
        var activeOrders = container.getState().getActiveChildOrders();
        assertTrue("should not be be accumulating multiple simultaneously active orders", activeOrders.size() <= 1);
    }

    // Test to check if the algorithm creates an order on the first tick

    @Test
    public void testAlgoCreatesOrderOnFirstTick() throws Exception {
        send(createTick());
        var childOrders = container.getState().getChildOrders();
        var order = childOrders.get(0);
        assertEquals("should join the best bid price from createTick()", 98L,
                order.getPrice());
        assertEquals("should match the best bid quantity from createTick()", 100L,
                order.getQuantity());
        var activeOrders = container.getState().getActiveChildOrders();
        assertEquals("order should still be resting, not filled", 1,
                activeOrders.size());
    }

    // Test to check cancelled orders

    @Test
    public void testAlgoCancelExistingOrderRatherThanStacking() throws Exception {
        send(createTick());
        send(createTick2());
        var activeOrders = container.getState().getActiveChildOrders();
        var childOrders = container.getState().getChildOrders();
        assertEquals(2, childOrders.size());
        assertEquals(1L, childOrders.stream()
                .filter(order -> order.getState() == OrderState.CANCELLED)
                .count());
        assertEquals("the order should now be cancelled, not active", 1,
                activeOrders.size());
    }

    // Test to see bid replacement on second tick
    @Test
    public void testCreateTick2UpdatesMarketData() throws Exception {
        send(createTick());
        assertEquals(98L, container.getState().getBidAt(0).price);
        send(createTick2());
        assertEquals(95L, container.getState().getBidAt(0).price);
        assertEquals(100L, container.getState().getBidAt(0).quantity);
        assertEquals(98L, container.getState().getAskAt(0).price);
    }

}

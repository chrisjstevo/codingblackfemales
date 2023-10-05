package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.OrderState;
import org.junit.Test;
import codingblackfemales.sotw.ChildOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        //  Get the initial state from the container
        var initialState = container.getState();
        assertEquals(0, initialState.getActiveChildOrders().size());
        assertEquals(0, initialState.getChildOrders().size());

        // Create a sample market data tick
        send(createTick());

        // Initial state assertions
        assertEquals(4, container.getState().getChildOrders().size());

        // Get the updated state
        var state = container.getState();

        // Check filled quantity i.e sum of quantity and canceled order count
        long filledQuantity = state.getChildOrders().stream().mapToLong(ChildOrder::getQuantity).sum();
        //long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getQuantity).reduce(Long::sum).get();
        long canceledOrderCount = state.getChildOrders().stream().filter(order -> order.getState() == OrderState.CANCELLED).count();


        // Assert filled quantity and canceled order count
        assertEquals(120, filledQuantity);
        assertEquals(1, canceledOrderCount); // Assuming only one order would be cancelled in total

        // Check set price of child orders
        long setPrice = state.getChildOrders().stream().map(ChildOrder::getPrice).reduce(Long::sum).orElse(0L);
        assertEquals(392, setPrice); // Assuming each order has a price of 392

        // Validate specific child order details based on algorithm logic
        long totalChildOrders = state.getChildOrders().size();
        long activeChildOrders = state.getActiveChildOrders().size();

        // Assert active child orders are less than or equal to 3
        assertTrue(activeChildOrders <= 3);

        // If there are fewer than 3 active child orders, assert a new order was created
        if (activeChildOrders < 3) {
            assertEquals(totalChildOrders, activeChildOrders + 1);
            // Assuming order price is between 91 and 98 based on bid level
            assertTrue(state.getActiveChildOrders().get(0).getPrice() >= 91 && state.getActiveChildOrders().get(0).getPrice() <= 98);
            // Assuming order quantity is 30
            assertEquals(30, state.getActiveChildOrders().get(0).getQuantity());
        }

        // If there are 3 or more active child orders, assert the oldest one was canceled
        if (activeChildOrders >= 3) {
            // Assuming the canceled order price is 98
            assertEquals(98, state.getActiveChildOrders().get(0).getPrice());
        }
    }
}

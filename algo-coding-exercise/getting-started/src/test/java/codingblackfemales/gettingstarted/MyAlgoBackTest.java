package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * This test plugs together all of the infrastructure, including the order book
 * (which you can trade against)
 * and the market data feed.
 *
 * If your algo adds orders to the book, they will reflect in your market data
 * coming back from the order book.
 *
 * If you cross the srpead (i.e. you BUY an order with a price which is == or >
 * askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the
 * childOrders of the state object.
 *
 * If you cancel the order your child order will show the order status as
 * cancelled in the childOrders of the state object.
 *
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest2 {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        // Create a sample market data tick.
        send(createTick());

        // Get the current state of the container.
        var state = container.getState();

        // Assertion that two child orders are creeated
        long totalChildOrders = state.getChildOrders().size();
        assertEquals(2, totalChildOrders);

        // Calculate the total filled quantity of all child orders.
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getQuantity).reduce(Long::sum).get();
        // Calculate the total set price of all child orders.
        long setPrice = state.getChildOrders().stream().map(ChildOrder::getPrice).reduce(Long::sum).orElse(0L);
       
        // Assert quantity and price of the orders created
        assertEquals(200, filledQuantity);
        assertEquals(200, setPrice);
        
        // Count the number of canceled child orders.
        long canceledOrderCount = state.getChildOrders().stream().filter(order -> order.getState() == OrderState.CANCELLED).count();
        // Assert that one order has been canceled
        assertEquals(1, canceledOrderCount);

        // Assert child order details based on algorithm logic
        long activeChildOrders = state.getActiveChildOrders().size();
        // Assert that there is one active order left
        assertEquals(1, activeChildOrders);
            
    }
}

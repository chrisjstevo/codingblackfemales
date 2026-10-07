package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import messages.order.Side;

import static org.junit.Assert.assertEquals;

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
public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        // This adds our algorithm logic to the container classes.
        return new MyAlgoLogic();
    }

    @Test
    public void testDispatchThroughSequencer() throws Exception {

        // Send the first market-data tick into the trading infrastructure.
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
}
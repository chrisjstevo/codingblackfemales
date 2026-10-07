package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import messages.order.Side;

import static org.junit.Assert.assertEquals;

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
public class MyAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        // create a sample market data tick....
        send(createTick());

        // ADD asserts when you have implemented your algo logic
        assertEquals(
                1,
                container.getState().getChildOrders().size());

        // Check that the child order is a BUY order.
        ChildOrder order = container.getState().getChildOrders().get(0);
        assertEquals(Side.BUY, order.getSide());

        // Check that the order quantity is 10.
        assertEquals(10, order.getQuantity());

        // Check that the order price is 98.
        assertEquals(98, order.getPrice());

        // when: market data moves towards us
        send(createTick2());

        // then: get the state
        var state = container.getState();

        // Check things like filled quantity, cancelled order count etc....
        assertEquals(
                1,
                state.getChildOrders().size());

        // .stream - Process the list of child orders
        // .map - gets the filled quantity from each order
        // .reduce - adds the quantities together
        // .get - retrieves the actual value
        long filledQuantity = state.getChildOrders().stream()
                .map(ChildOrder::getFilledQuantity)
                .reduce(Long::sum)
                .get();

        // Check that algo state was updated to reflect fills
        assertEquals(10, filledQuantity);
    }

}
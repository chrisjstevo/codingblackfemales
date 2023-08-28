package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;

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
 * If you cross the spread (i.e. you BUY an order with a price which is == or >
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
        assertEquals(container.getState().getChildOrders().size(), 1);
        // when there was no match
        long filledQuantity3 = container.getState().getChildOrders().stream().map(ChildOrder::getFilledQuantity)
                .reduce(Long::sum)
                .get();
        assertEquals(0, filledQuantity3);
        // when: market data moves towards us
        send(createTick2());

        // then: get the state
        var state = container.getState();

        // Check things like filled quantity, cancelled order count etc....
        // checking that we placed an order when there are no active orders
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum)
                .get();
        // and: check that our algo state was updated to reflect our fills when the
        // market data
        assertEquals(501, filledQuantity);
        assertEquals(container.getState().getChildOrders().size(), 2);

        send(createTick3());

        // cancel order
        /* what is this really testing for? */
        // Integer cancelledOrder =
        // state.getActiveChildOrders().stream().map(ChildOrder::getState)
        // .findFirst().get();
        // assertEquals((int) cancelledOrder, 1);
        // how do i then check that
        // check that the order was cancelled when the prices are not equal

        // check if no action is performed when the prices are equal
        // - we are returning NoAction.noAction which means behave as expected
        long filledQuantity2 = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum)
                .get();
        assertEquals(1002, filledQuantity2);
        assertEquals(container.getState().getChildOrders().size(), 3);

        ;
    }

}

package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 * This test plugs together all of the infrastructure, including the order book (which you can trade against)
 * and the market data feed.
 *
 * If your algo adds orders to the book, they will reflect in your market data coming back from the order book.
 *
 * If you cross the spread (i.e. you BUY an order with a price which is == or > askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the childOrders of the state object).
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
        //create a sample market data tick....

        send(createTick());

        //ADD asserts when you have implemented your algo logic
        assertEquals(container.getState().getChildOrders().size(), 3);
        assertEquals(container.getState().getActiveChildOrders().size(), 2);

        send(createTick2());

        //then: get the state
        var state = container.getState();

        //Check things like filled quantity, cancelled order count etc....
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        long cancelledOrderCount = state.getChildOrders().size() - state.getActiveChildOrders().size();

        //and: check that our algo state was updated to reflect our fills when the market data changes
        assertEquals(200, filledQuantity);

        // we also check that the number of cancelled orders is 1
        assertEquals(1, cancelledOrderCount);
        assertEquals(container.getState().getActiveChildOrders().size(), 2);

        //When the market moves away from us
        send(createTick3());

        //When the market moves towards us and we can make a profit
        send(createTick4());

        //once the market is updated we check that we have made another childOrder to sell at a profit
        assertEquals(container.getState().getActiveChildOrders().size(), 3);
        assertEquals(container.getState().getChildOrders().size(), 4);

        send(createTick5());
    }

}

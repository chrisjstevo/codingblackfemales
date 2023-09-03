package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;

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
        // send(createTick());

        // simple assert to check we had 3 orders created
        // assertEquals(container.getState().getChildOrders().size(), 1);

        // when: market data moves towards us
        // send(createTick2());

        // then: get the state
        // var state = container.getState();

        // Check things like filled quantity, cancelled order count etc....
        // long filledQuantity =
        // state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum)
        // .get();
        // and: check that our algo state was updated to reflect our fills when the
        // market data
        // assertEquals(50, filledQuantity);
    }
}

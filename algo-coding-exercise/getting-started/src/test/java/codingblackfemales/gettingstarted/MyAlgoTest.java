package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.junit.Test;

/**
 * This test is designed to check your algo behavior in isolation of the order
 * book.
 * <p>
 * You can tick in market data messages by creating new versions of createTick()
 * (ex. createTick2, createTickMore etc..)
 * <p>
 * You should then add behaviour to your algo to respond to that market data by
 * creating or cancelling child orders.
 * <p>
 * When you are comfortable you algo does what you expect, then you can move on
 * to creating the MyAlgoBackTest.
 */
public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        // this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }

    // This test checks if my algorithm successfully creates and cancels 6 child
    // orders.

    @Test
    public void testDispatchThroughSequencer() throws Exception {

        // create a sample market data tick....
        send(createTick());

        var myChildOrders = container.getState().getChildOrders();

        // simple assert to check we had 6 orders created
        assertEquals(myChildOrders.size(), 6);

        // when: market data moves towards us
        send(createTick2());

        // get filled quantity
        long filledQuantity = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
                .reduce(Long::sum)
                .get();

        // check that there are no fullfilled order
        assertEquals(0, filledQuantity);

        // get cancelled order
        long cancelledOrders = myChildOrders.stream()
                .filter(order -> order.getState() == OrderState.CANCELLED).collect(Collectors.toList())
                .size();

        // check that all 6 orders were cancelled
        assertEquals(6, cancelledOrders);
    }
}

package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * This test plugs together all of the infrastructure, including the order book
 * (which you can trade against)
 * and the market data feed.
 * <p>
 * If your algo adds orders to the book, they will reflect in your market data
 * coming back from the order book.
 * <p>
 * If you cross the spread (i.e. you BUY an order with a price which is == or >
 * askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the
 * childOrders of the state object.
 * <p>
 * If you cancel the order your child order will show the order status as
 * cancelled in the childOrders of the state object.
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    // we are assuming the market would always go down
    @Test
    public void testExampleBackTest() throws Exception {
        // create a sample market data tick....
        send(createTick());
        var myChildOrders = container.getState().getChildOrders();
        // I expect to have created one child order
        assertEquals(myChildOrders.size(), 1);
        // but i don't expect it to have been filled
        long filledQuantity = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
                .reduce(Long::sum)
                .get();
        assertEquals(0, filledQuantity);

        send(createTick2());
        // I still expect to have one order, but to have the order cancelled
        // because the new best price is lower than the existing order price
        assertEquals(myChildOrders.size(), 1);
        // based on this I don't expect it to have been filled
        long filledQuantity2 = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
                .reduce(Long::sum)
                .get();

        assertEquals(0, filledQuantity2);

        send(createTick3());
        // I expect it to create 5 more orders +My the existing 1 order
        assertEquals(container.getState().getChildOrders().size(), 6);

        long filledQuantity3 = myChildOrders.stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum)
                .get();
        // I expect it to fill the 5 orders because the market moved towards us
        assertEquals(1000, filledQuantity3);

        // send(createTick4());
        // // checking for cancelled order
        // long filledQuantity4 =
        // state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum)
        // .get();
        // assertEquals(401, filledQuantity4);
        // assertEquals(container.getState().getChildOrders().size(), 4);
        // ;
    }

}

package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildFill;
import org.junit.Test;
import codingblackfemales.sotw.ChildOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * This test plugs together all of the infrastructure, including the order book (which you can trade against)
 * and the market data feed.
 *
 * If your algo adds orders to the book, they will reflect in your market data coming back from the order book.
 *
 * If you cross the srpead (i.e. you BUY an order with a price which is == or > askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the childOrders of the state object.
 *
 * If you cancel the order your child order will show the order status as cancelled in the childOrders of the state object.
 *
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoBackTest.class);


    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        //create a sample market data tick....
        send(createTick());

        //ADD asserts when you have implemented your algo logic
        assertEquals(container.getState().getChildOrders().size(), 12);
        assertEquals(container.getState().getActiveChildOrders().size(), 3);

        //when: market data moves towards us
        logger.info("market data moves towards us");
        send(createTick2());

        //then: get the state
        logger.info("Get the state");
        var state = container.getState();

        //Check things like filled quantity, cancelled order count etc....
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        logger.info("Filled quantity is: " + filledQuantity);

        long quantity = state.getChildOrders().stream().map(ChildOrder::getQuantity).reduce(Long::sum).get();
        logger.info("Quantity of all orders (filled and non-filled) is: " + quantity);

        long price = state.getChildOrders().stream().map(ChildOrder::getPrice).reduce(Long::sum).get();
        logger.info("Price is: " + price);

        long filledPrice = state.getActiveChildOrders().stream().map(ChildOrder::getPrice).reduce(Long::sum).get();
        var activeOrderCount = state.getActiveChildOrders().size();
        long averageFilledPrice = filledPrice/activeOrderCount;
        logger.info("Filled price is: " + filledPrice);
        logger.info("Average filled price is: " + averageFilledPrice);

        //and: check that our algo state was updated to reflect our fills when the market data
        assertEquals(300, filledQuantity);
    }

}

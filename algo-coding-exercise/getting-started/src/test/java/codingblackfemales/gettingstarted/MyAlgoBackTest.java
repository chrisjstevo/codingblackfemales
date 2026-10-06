package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testOrderIsFilledWhenMarketMoves() throws Exception {
      
        // Send the initial market-data tick
        // The algo creates a BUY order at the best bid
        send(createTick());

        // Send a second market-data tick where the market moves towards the BUY order
        // The backtest should match the order and generate a fill
        send(createTick2());

       
        // Get the current algo state after the market has moved
        var state = container.getState();

        // Check that the BUY order created by the algo was filled for the expected quantity
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        assertEquals(100, filledQuantity);
    }

}

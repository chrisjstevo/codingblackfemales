package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import org.junit.Test;

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

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    // scenario 1: cheap market data creates BUY child orders in the simulated order book
    @Test
    public void createBuyChildOrdersInSimulatedOrderBook() throws Exception {
        send(createTick());
        assertEquals(3, container.getState().getChildOrders().size());
    }

    // scenario 2: later market data moves towards the algo and fills active child orders
    @Test 
    public void fillActiveChildOrders() throws Exception {
        send(createTick());
        send(createTick2());

        var state = container.getState();
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        
        assertEquals(225, filledQuantity);
    }

    // scenario 3: filled quantity is reflected back into the algo state
    @Test 
    public void fillQuantityReflectedInAlgoState() throws Exception {
        send(createTick());
        send(createTick2());

        var state = container.getState();

        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();

        assertEquals(225, filledQuantity);
    }   
    
    // scenario 4: cancellation logic cancels open child orders when the market is no longer favourable
    @Test 
    public void cancellationLogicUpdatesChildOrderStatus() throws Exception{
        send(createTick());
        send(createTick3());
        send(createTick3());
        send(createTick3());

        var state = container.getState();

        long cancelledOrders = state.getChildOrders().stream()
                .filter(childOrder -> childOrder.getState() == OrderState.CANCELLED)
                .count();
        assertEquals(3, cancelledOrders);
    }
}

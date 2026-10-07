package codingblackfemales.gettingstarted;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.OrderState;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * This test is designed to check your algo behavior in isolation of the order book.
 *
 * You can tick in market data messages by creating new versions of createTick() (ex. createTick2, createTickMore etc..)
 *
 * You should then add behaviour to your algo to respond to that market data by creating or cancelling child orders.
 *
 * When you are comfortable you algo does what you expect, then you can move on to creating the MyAlgoBackTest.
 *
 */
public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        //this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }

    // scenario 1: cheap bid triggers BUY orders
    @Test 
    public void createsBuyOrdersWhenBidPriceIsBelowTargetBuyPrice() throws Exception {
        send(createTick());
        assertEquals(3, container.getState().getChildOrders().size());
        
    }

    // scenario 2: expensive ask triggers SELL orders
    @Test 
    public void createsSellOrdersWhenAskIsAboveTargetSellPrice() throws Exception {
        send(createTick2());
        assertEquals(3, container.getState().getActiveChildOrders().size());
    }

    // scenario 3: no buy/sell conditions met triggers no action
    @Test 
    public void takesNoActionWhenMarketDoesNotMeetAnyConditions() throws Exception {
        send(createTick3());
        assertEquals(0, container.getState().getActiveChildOrders().size());
    }

    // scenario 4: existing BUY orders are cancelled when the market is no longer cheap
    @Test
    public void cancelsBuyOrdersWhenBidPriceIsNoLongerBelowTargetBuyPrice() throws Exception {
        send(createTick());
        send(createTick3());

        long cancelledOrders = container.getState().getChildOrders().stream()
                .filter(childOrder -> childOrder.getState() == OrderState.CANCELLED)
                .count();

        assertEquals(3, cancelledOrders);
        assertEquals(0, container.getState().getActiveChildOrders().size());
    }
}

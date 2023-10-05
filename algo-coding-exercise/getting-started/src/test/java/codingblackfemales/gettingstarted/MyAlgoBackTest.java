package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.marketdata.gen.MarketDataGenerator;
import codingblackfemales.orderbook.order.Order;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.read.ListAppender;

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

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        //create a sample market data tick....
        send(createTick());

        //ADD asserts when you have implemented your algo logic
        // assertEquals(container.getState().getChildOrders().size(), 0); normal logic
        assertEquals(container.getState().getChildOrders().size(), 16);

        //when: market data moves towards us
        send(createTick2());

        //then: get the state
        var state = container.getState();

        // //Check things like filled quantity, cancelled order count etc....
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        // //and: check that our algo state was updated to reflect our fills when the market data
        assertEquals(501, filledQuantity);
    }
 
    @Test
    public void monteCarloSim() throws Exception {
        // /we use the monte Carlo simulation tosimulate random market situations to see how our also reacts
        int simulations = 250;
        int orderTarget = 75;

        for(int i = 0; i < simulations; i++){
            send(createTickMonteCarlo());
        }
        var state = container.getState();

        int totalOrderCount = state.getChildOrders().size();

        if(orderTarget >= totalOrderCount){
            logger.info("[MYALGOBACKTEST] orders have exceeded target.");
        }
    }

// This test is expected to fail as it checks if market is open at 7:30 am.
    @Test
    public void testMarketClosed() throws Exception {
        TimedLogic timedLogic = new TimedLogic(null);
        timedLogic.setCurrentTime(LocalTime.of(7,30));
        assertFalse(timedLogic.isMarketOpen());
    }

    // This test is expected to pass as it checks if market is open at 15:30pm.
    @Test
    public void testMarketOpen() throws Exception {
        TimedLogic timedLogic = new TimedLogic(null);
        timedLogic.setCurrentTime(LocalTime.of(15,30));
        assertTrue(timedLogic.isMarketOpen());
    }

    

    @Test
    public void testsCancelledOrders() throws Exception {

        send(createTickCancel());

        var state = container.getState();

        long cancelledOrders = state.getChildOrders().stream().filter(order -> order.getState() == OrderState.CANCELLED).count();
        
        assertEquals(0, cancelledOrders);

    }

    @Test
    public void testOrderPlacement() throws Exception {

        send(orderPlacedTick());

        var state = container.getState();

        var activeOrders = state.getActiveChildOrders();

        assertTrue(activeOrders.isEmpty());
        

    }

}

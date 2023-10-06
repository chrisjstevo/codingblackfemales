package codingblackfemales.gettingstarted;

import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.orderbook.order.Order;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.marketdata.BookUpdateEncoder;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.Venue;
import messages.order.Side;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.rmi.UnexpectedException;
import java.time.DayOfWeek;
import java.time.LocalTime;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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


    @Test
    public void testDispatchThroughSequencer() throws Exception {

        //create a sample market data tick....
        send(createTick());

        //simple assert to check we had 3 orders created
        // assertEquals(container.getState().getChildOrders().size(), 45); NormalMyLogic
        assertEquals(container.getState().getChildOrders().size(), 16);
    }

    @Test
    public void checkIfOrdersCancelled() throws Exception{
        // checks to make sure that once terms are met, child order gets cancelled.

        send(createTick());
        int initialOrderCount = 15;
        // what is the initial order count

        // int expectedCanceledOrderCount = 40; NormalMyAlgoLogic
        int expectedCanceledOrderCount = 9;
        // how many order should be cancelled
        var state = container.getState();

        assertEquals(expectedCanceledOrderCount, initialOrderCount - state.getActiveChildOrders().size());
    }

    
    @Test
    public void testMaximumOrderCount() throws Exception {
        // test makes sure that maximum orders accepted are 45, 
        int maxAcceptedOrders = 16;

        // Create a sample market data tick that should result in orders being placed
        send(createMaxOrderTick(maxAcceptedOrders));

        // Verify that the number of orders created does not exceed the maximum order count
        int NumOfActualOrders = container.getState().getChildOrders().size();
        assertEquals(maxAcceptedOrders, NumOfActualOrders);
    }

    @Test
    public void marketIsOpen() throws Exception{
        TimedLogic timedLogic = new TimedLogic(null);
        // checks to see is uk stockmarket is open on a normal working week without bank holidays.

        timedLogic.setDayOfWeek(DayOfWeek.SATURDAY);

        timedLogic.setCurrentTime(LocalTime.of(16, 31));
        assertFalse(!timedLogic.isMarketOpen());
    }

    @Test
    public void noActionTest() throws Exception{
        // test makes sure no action is taken when the conditions are met

        // final var activeOrders = state.getActiveChildOrders();

        int numActiveOrders = 7; //Normal MyAlgoLogic 
        // int numActiveOrders = 0;

        send(noActionTick(numActiveOrders));
    //    boolean noActionOptionPresent = container.getState().getOptions().contains(Option.NO_ACTION);

        var state = container.getState();
        
        assertEquals(6, state.getActiveChildOrders().size());
        // might need to check this sor state.activeOrders()
    }
    
    @Test
    public void testLogging() throws Exception{
        Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);
        logger.info("We are testing the logging function.");
        logger.warn("We are testing the warnig method that comes with the logger.");
        // logger.error("This checks errors, " + Exception);
    }

}



// code is currently working
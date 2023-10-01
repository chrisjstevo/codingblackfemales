package codingblackfemales.gettingstarted;

import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.orderbook.order.Order;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.marketdata.BookUpdateEncoder;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.Venue;
import messages.order.Side;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.ByteBuffer;

import org.agrona.concurrent.UnsafeBuffer;
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


    @Test
    public void testDispatchThroughSequencer() throws Exception {

        //create a sample market data tick....
        send(createTick());

        //simple assert to check we had 3 orders created
        assertEquals(container.getState().getChildOrders().size(), 45);
    }

    @Test
    public void checkIfOrdersCancelled() throws Exception{
        // checks to make sure that once terms are met, child order gets cancelled.

        send(createTick());
        int initialOrderCount = 45;
        // what is the initial ordr count

        int expectedCanceledOrderCount = 40;
        // how many order should be cancelled
        var state = container.getState();

        assertEquals(expectedCanceledOrderCount, initialOrderCount - state.getActiveChildOrders().size());
    }

    
    @Test
    public void testMaximumOrderCount() throws Exception {
        // test makes sure that maximum orders accepted are 45, 
        int maxAcceptedOrders = 45;

        // Create a sample market data tick that should result in orders being placed
        send(createMaxOrderTick(maxAcceptedOrders));

        // Verify that the number of orders created does not exceed the maximum order count
        int NumOfActualOrders = container.getState().getChildOrders().size();
        assertEquals(maxAcceptedOrders, NumOfActualOrders);
    }

    
    

    // @Test
    // public void noActionTest() throws Exception{
    //     // test makes sure no action is taken when the conditions are met

    //     int ordersNotCanx = 5;
    // send(noActionTick(ordersNotCanx));
    //    boolean noActionOptionPresent = container.getState().getOptions().contains(Option.NO_ACTION);

    //     var state = container.getState();

    //     assertEquals(0, container.getState().getChildOrders().size());
        
    // }

    

//         // should test if order is less than 5 and option is not present.

//         // In this test:

// // You create a sample market data tick using the createMarketDataForNoAction method. Adjust the properties of marketData to simulate the scenario where no action should be taken.

// // You send the market data tick to your algorithm.

// // You verify that no orders were created or canceled by checking the size of the getChildOrders list in the algorithm's state.

// // Make sure to customize the createMarketDataForNoAction method and the properties of marketData to match the specific conditions under which your algorithm should not take any action.


//     // @Test
//     // public void priceTooHigh() throws Exception{

//     //     send(creatHighTick());

//     //     assertEquals(0, container.getState().getChildOrders().size());
//     // }

//     // @Test
//     // public void testingFallingPrice(){
//     //     send(createFallingPriceTick());

//     //     int currentOrderCount = container.getState().getChildOrders().size();
//     //      // You can adjust the expected behavior based on your algorithm's logic
//     //     // For example, if the price falls, you might expect the algorithm to create a new order
//     //     assertEquals(expectedOrderCount, currentOrderCount);
//     // }

    // @Test
    // public void testMaxOrder() throws Exception{

    //     int maXOrdersBeforeCanx = 5;
    //     // Create a sample market data tick with less than 5 active orders
    //     MarketData marketData = createMarketDataWithActiveOrders(maXOrdersBeforeCanx - 1);

    //     send(new tick());
    //     var state = container.getState();
    //     final var option = state.getActiveChildOrders().stream().findFirst();

    //      // Verify that the "no action" option is not present
    //     assertFalse(option.isPresent());

    // }


}


// *two complete tests done with ticks created. create 3 more. not committed
// code is currently working
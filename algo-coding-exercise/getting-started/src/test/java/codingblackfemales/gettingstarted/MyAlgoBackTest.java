package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.marketdata.gen.MarketDataGenerator;
import codingblackfemales.orderbook.order.Order;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
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

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        //create a sample market data tick....
        send(createTick());

        //ADD asserts when you have implemented your algo logic
        assertEquals(container.getState().getChildOrders().size(), 45);

        //when: market data moves towards us
        send(createTick2());

        //then: get the state
        var state = container.getState();

        //Check things like filled quantity, cancelled order count etc....
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        //and: check that our algo state was updated to reflect our fills when the market data
        assertEquals(501, filledQuantity);
    }

    

    // @Test
    // public void testsCancelledOrders() throws Exception {
    //      // Create a sample market data update that triggers order cancellation
    //     MarketData marketData = new MarketData();
    //     // Set market data properties to trigger order cancellation, e.g., a new best bid/ask that invalidates existing orders

    //     // Simulate the market data update
    //     orderBook.updateMarketData(marketData);

    //     // Call your algorithm's evaluate method to handle the market data update
    //     algoLogic.evaluate(algoState);
    //     MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
    //     myAlgoLogic.evaluate(state.childOrders());

    //     // Verify if orders were canceled as expected
    //     // For example, you can check if the canceled orders have the correct state
    //     // You may need to adapt this verification based on your algorithm's behavior

    //     for (Order order : algoState.getCanceledChildOrders()) {
    //         assertEquals(OrderState.CANCELED, order.getState());
    //         assertTrue(order.getEventType() == OrderEventType.CANCELED || order.getEventType() == OrderEventType.EXPIRED);
    //     }
    // }

    // @Test
    // public void testOrderPlacement() throws Exception {
    //     // Create a sample market data tick with conditions that should trigger an order
    //     MarketData marketData = createMarketDataForOrderPlacement();
    //     //1. createMarketDataForOrderPlacement() is used to create a sample market data tick with conditions that should trigger your algorithm to place an order. You should customize this method to set the bid/ask prices, volumes, or any other relevant data based on your algorithm's logic.

    //     // Send the market data tick to your algorithm
    //     send(marketData);
    //     // 2.she send(marketData) statement simulates sending this market data to your algorithm.

    //     // Get the state after processing the market data
    //     var state = container.getState();
    //     // 3. After processing the market data, you retrieve the algorithm's state using container.getState().
    //     // Check if your algorithm placed the expected order(s)
    //     int expectedOrderCount = 1; // Update with the expected order count
    //     assertEquals(expectedOrderCount, state.getChildOrders().size());
    //     // 4.Finally, you assert whether the algorithm placed the expected number of orders based on the market data conditions.
    // }

    // private MarketData createMarketDataForOrderPlacement() {
    //     // Create a sample market data tick that triggers your algorithm to place an order
    //     MarketData marketData = new MarketData();
    //     // Set relevant data in the market data, e.g., bid/ask prices, volumes, etc.
    //     marketData.setBidPrice(100); // Set the bid price as needed for your strategy
    //     marketData.setAskPrice(101); // Set the ask price as needed for your strategy
    //     // Add any other necessary data to simulate market conditions

    //     return marketData;
    // }

//      @Test
//     public void testLogging() {
//         // Create a logger for the class you want to test
//         Logger logger = (Logger) LoggerFactory.getLogger(MyAlgoLogic.class);

//         // Create a ListAppender to capture log messages
//         ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> listAppender = new ListAppender<>();
//         listAppender.start();

//         // Attach the ListAppender to the logger
//         logger.addAppender(listAppender);

//         // Call the method that generates log messages
//         myAlgoLogic.myMethod();

//         // Stop the ListAppender to retrieve the log messages
//         listAppender.stop();

//         // Get the captured log messages
//         List<ch.qos.logback.classic.spi.ILoggingEvent> logEvents = listAppender.list;

//         // Assert log messages
//         assertEquals("Debug log message", logEvents.get(0).getMessage());
//         assertEquals(Level.DEBUG, logEvents.get(0).getLevel());

//         assertEquals("Info log message", logEvents.get(1).getMessage());
//         assertEquals(Level.INFO, logEvents.get(1).getLevel());

//         assertEquals("Warn log message", logEvents.get(2).getMessage());
//         assertEquals(Level.WARN, logEvents.get(2).getLevel());

//         assertEquals("Error log message", logEvents.get(3).getMessage());
//         assertEquals(Level.ERROR, logEvents.get(3).getLevel());

// //         n this test:

// // We create a logger for the MyAlgoLogic class.
// // We set up a ListAppender to capture log messages.
// // We attach the ListAppender to the logger.
// // We call the myMethod of MyAlgoLogic, which generates log messages.
// // We stop the ListAppender to retrieve the log messages.
// // We assert the log messages and their levels to ensure they match what you expect.
// // Make sure to adjust the logger configuration and assertions according to your specific logging statements in MyAlgoLogic.
//     }


    // }

    // private MarketData createMarketDataWithHighAsk() {
    //     // Create a sample market data tick with a high ask price
    //     MarketData marketData = new MarketData();
    //     marketData.setAskPrice(1000); // Set a high ask price that exceeds your threshold

    //     // You may need to set other properties of marketData as well depending on your algorithm's requirements

    //     return marketData;
    // }

    // private MarketData createMarketDataWithFallingPrice() {
    //     // Create a sample market data tick with a falling price
    //     MarketData marketData = new MarketData();
    //     marketData.setBidPrice(950); // Set a lower bid price than the previous market data

    //     // You may need to set other properties of marketData as well depending on your algorithm's requirements

    //     return marketData;
    // }

}

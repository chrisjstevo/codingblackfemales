package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import messages.order.Side;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class StretchAlgoBackTest extends AbstractStretchAlgoBackTest{

    @Override
    public AlgoLogic createAlgoLogic() {
        return new StretchAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {

        // Get the initial state from the container
        var initialState = container.getState();
        assertEquals(0, initialState.getActiveChildOrders().size());
        assertEquals(0, initialState.getChildOrders().size());

        // Creating sample market data ticks
        send(createTick());
        send(createTick2());
        send(createTick3());
        send(createTick4());
        send(createTick5());
        send(createTick6());
        send(createTick7());

        // Get the updated state from the container after processing the ticks
        var updatedState = container.getState();
        // Perform assertions based on the updated state
        assertEquals(4, updatedState.getActiveChildOrders().size());
        assertEquals(4, updatedState.getChildOrders().size());

        // Check the generated orders
        var createdOrders = updatedState.getChildOrders();
        for (ChildOrder order : createdOrders) {
            assertEquals(100, order.getQuantity()); // Assuming order quantity is always 100
            assertTrue(order.getPrice() > 0); // Order price should be a positive value
        }


        // Check specific bid and ask prices and quantities
        long firstBidPrice = updatedState.getBidAt(0).price;
        long firstAskQuantity = updatedState.getAskAt(0).quantity;
        assertEquals(48L, firstBidPrice); // Assuming a delta of 0.001 is acceptable for double comparison
        assertEquals(101L, firstAskQuantity);


        // Test that the calculate list difference is working
        StretchAlgoLogic logic = new StretchAlgoLogic();
        List<Double> prices = List.of(100.0, 150.0, 120.0, 170.0);
        double difference = logic.calculateListDifference(prices);
        assertEquals(70.0, difference, 0.01);


        // Testing the method calculate weighted average is working
        StretchAlgoLogic.MarketOrders order1 = logic.new MarketOrders(100L, 10L);
        StretchAlgoLogic.MarketOrders order2 = logic.new MarketOrders(150L, 5L);
        double weightedAverage = logic.calculateWeightedAverage(List.of(order1, order2));
        //using an addition of the above current data current
        assertEquals(116.67, weightedAverage, 0.01);

        // Check the last active child order created
        var lastActiveChildOrder = updatedState.getActiveChildOrders().get(2);
        //assuming the market has made a decline
        assertEquals(Side.BUY, lastActiveChildOrder.getSide());
        assertEquals(100L, lastActiveChildOrder.getQuantity());
        assertEquals(98L, lastActiveChildOrder.getPrice());

        // Check for stable market condition (no new orders should be created)
        send(createTick()); //tick that makes tick stable
        var stableMarketState = container.getState();
        assertEquals(updatedState.getActiveChildOrders().size(), stableMarketState.getActiveChildOrders().size());
        assertEquals(updatedState.getChildOrders().size(), stableMarketState.getChildOrders().size());

        // Creating a tick that represents an upward trend (more buy orders)
        send(createUpwardMarketTick());
        var upwardTrendState = container.getState();
        assertEquals(stableMarketState.getActiveChildOrders().size(), upwardTrendState.getActiveChildOrders().size());
        assertEquals(Side.BUY, upwardTrendState.getActiveChildOrders().get(0).getSide());

        // Creating a tick that represents a downward trend (more sell orders)
        send(createDownwardMarketTick());
        var downwardTrendState = container.getState();
        assertEquals(stableMarketState.getActiveChildOrders().size(), downwardTrendState.getActiveChildOrders().size());

        //NOT WORKING AS SHOULD
        assertEquals(Side.SELL, downwardTrendState.getActiveChildOrders().get(0).getSide());
    }
}

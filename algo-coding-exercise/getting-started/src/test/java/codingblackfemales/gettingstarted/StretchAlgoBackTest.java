package codingblackfemales.gettingstarted;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;
import codingblackfemales.sotw.ChildOrder;
import messages.order.Side;

public class StretchAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public StretchAlgoLogic createAlgoLogic() {
        return new StretchAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {

        // Initialize the state of the algorithm container.
        var state = container.getState();

        // Simulate market data movements
        send(createTick());
        send(createTick2());
        send(createTick3());
        send(createTick4());
        send(createTick5());

        // Ensure that 3 child orders have been created.
        assertEquals(state.getChildOrders().size(), 3);

        // Check the last active child order created.
        var lastActiveChildOrder = state.getActiveChildOrders().get(0);

        // Calculate the total quantity of child orders.
        long totalQuantity = state.getChildOrders().stream().mapToLong(ChildOrder::getQuantity).sum();

        // Verify the properties of the last child order.
        // Total quantity of child orders
        assertEquals(300, totalQuantity);
        // Expected order side (SELL)
        assertEquals(Side.SELL, lastActiveChildOrder.getSide());
        // Expected quantity
        assertEquals(100L, lastActiveChildOrder.getQuantity());
        // Expected price
        assertEquals(97L, lastActiveChildOrder.getPrice());

        // testing calculate list difference is working
        StretchAlgoLogic logic = new StretchAlgoLogic();
        List<Double> prices = List.of(100.0, 150.0, 120.0, 170.0);
        double difference = logic.calculateListDifference(prices);
        assertEquals(70.0, difference, 0.01);

        // unit testing the method calculate weighted average is working
        StretchAlgoLogic.OrderBookLevel order1 = logic.new OrderBookLevel(100L, 10L);
        StretchAlgoLogic.OrderBookLevel order2 = logic.new OrderBookLevel(150L, 5L);
        double weightedAverage = logic.calculateWeightedAverage(List.of(order1, order2));

        assertEquals(116.0, weightedAverage, 0.01);

        // Check for a stable market condition (no new orders should be created)
        // Simulate a stable market condition
        send(createTickStable());
        var stableMarketState = container.getState();
        // Ensure that the number of active child orders and total child orders remains the same.
        assertEquals(state.getActiveChildOrders().size(),
                stableMarketState.getActiveChildOrders().size());
        assertEquals(state.getChildOrders().size(),
                stableMarketState.getChildOrders().size());

        // Creating a tick that represents a downward trend (more sell orders).
        send(createTickDownTrend());
        var downwardTrendState = container.getState();
        // Ensure that the number of active child orders remains the same and the side is SELL.
        assertEquals(stableMarketState.getActiveChildOrders().size(),
                downwardTrendState.getActiveChildOrders().size());
        assertEquals(Side.SELL,
                downwardTrendState.getActiveChildOrders().get(0).getSide());

        // Creating a tick that represents an upward trend (more buy orders)
        // send(createTickUpTrend());
        // var upwardTrendState = container.getState();
        // // Ensure that the number of active child orders remains the same and the side is BUY.
        // assertEquals(stableMarketState.getActiveChildOrders().size(),
        // upwardTrendState.getActiveChildOrders().size());
        // assertEquals(Side.BUY,
        // upwardTrendState.getActiveChildOrders().get(0).getSide());

    }
}

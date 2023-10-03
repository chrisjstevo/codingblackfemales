package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MovingAverageBackTest extends AbstractMovingAverageBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MovingAverage();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        // create a sample market data tick....
        // Note I created a custom send so that I can send array buffers
        customSend(createSampleMarketDataTick(10));

        assertNotNull(container.getState().getBidAt(0));
        assertNotNull(container.getState().getAskAt(0));
        long pendingOrders = container.getState().getChildOrders().stream()
                .filter(order -> order.getState() == OrderState.PENDING).collect(Collectors.toList())
                .size();
        assertEquals(2, pendingOrders);

    }

}

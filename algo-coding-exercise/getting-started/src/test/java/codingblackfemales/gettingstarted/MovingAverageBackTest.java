package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import org.junit.Test;

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

        // todo - check if only one order is pending

        assertEquals(1, OrderState.PENDING);
        assertNotNull(container.getState().getBidAt(0));
        assertNotNull(container.getState().getAskAt(0));

    }
}

package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;

import codingblackfemales.sotw.OrderState;
import messages.order.Side;
import org.junit.Test;

import static org.junit.Assert.*;

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

                // Simple assert to validate that ask level is not null
                assertNotNull(container.getState().getAskAt(0));

                // check that all pending orders can only be equals to 2 or less than 2
                long pendingOrders = container.getState().getActiveChildOrders().stream()
                                .filter(order -> order.getState() == OrderState.PENDING).toList()
                                .size();
                assertTrue(pendingOrders <= 2);

                // it should never have more than 1 pending buy order
                long pendingBuyOrder = container.getState().getActiveChildOrders().stream()
                                .filter(order -> order.getState() == OrderState.PENDING)
                                .filter(order -> order.getSide() == Side.BUY)
                                .toList()
                                .size();
                assertFalse(pendingBuyOrder > 1);

        }

}

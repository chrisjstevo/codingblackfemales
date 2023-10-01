package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MovingAverageBackTest extends AbstractMovingAverageBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MovingAverage();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        // create a sample market data tick....
        // Note I created a custom send so that I can send array buffers
        // customSend(createSampleMarketDataTick(10));
        // will it successfully sell if i generate 100 ticks?
        customSend(createSampleMarketDataTick(10));
        // test for how many child orders was created
        var myChildOrders = container.getState().getChildOrders();

        var actualActiveOrders = container.getState().getActiveChildOrders().size();
        var expectedActiveOrders = myChildOrders.size();
        assertEquals(expectedActiveOrders, actualActiveOrders);

        long filledQuantity = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
                .reduce(Long::sum)
                .get();
        long expectedFilledQuantity = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
                .reduce(Long::sum)
                .get();
        assertEquals(expectedFilledQuantity, filledQuantity);

        // todo- if possible test for bid filledQuantity and ask filledQuantity

    }
}

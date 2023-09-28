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
        //create a sample market data tick....
        send(createSampleMarketDataTick());

        var myChildOrders = container.getState().getChildOrders();
        assertEquals(myChildOrders.size(), 1);

        send(createSampleMarketDataTick2());
        long filledQuantity = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
                .reduce(Long::sum)
                .get();
        assertEquals(2600, filledQuantity);

        send(createSampleMarketDataTick3());


        send(createSampleMarketDataTick4());


        send(createSampleMarketDataTick5());


        send(createSampleMarketDataTick6());


        send(createSampleMarketDataTick7());

    }
}

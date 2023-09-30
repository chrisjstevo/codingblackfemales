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
//        Note I created a custom send so that I can send array buffers
        customSend(createSampleMarketDataTick(10));

        var myChildOrders = container.getState().getChildOrders();


//        long filledQuantity = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
//                .reduce(Long::sum)
//                .get();
//        assertEquals(0, filledQuantity);


    }
}

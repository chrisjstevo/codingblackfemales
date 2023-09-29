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

//        | Sample |  Bids |       Asks      |
//|--------|----------|-----------------|
//|   1    | 102L	| 104L |
//|   2    | 105L	| 103L  |
//|   3    | 103L	| 104L |
//|   4    | 104L	| 102L |
//|   5    | 101L	 | 105L |
//|   6    | 106L  	| 106L|
//|   7    | 110L	| 109L  |
        var myChildOrders = container.getState().getChildOrders();


        send(createSampleMarketDataTick2());
//        long filledQuantity = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
//                .reduce(Long::sum)
//                .get();
//        assertEquals(0, filledQuantity);

        send(createSampleMarketDataTick3());
//        assertEquals(myChildOrders.size(), 50);

        send(createSampleMarketDataTick4());


        send(createSampleMarketDataTick5());


        send(createSampleMarketDataTick6());

        send(createSampleMarketDataTick7());

    }
}

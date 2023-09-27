package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import org.junit.Test;

public class MovingAverageBackTest extends AbstractMovingAverageBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MovingAverage();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        //create a sample market data tick....
        send(createSampleMarketDataTick());
        send(createSampleMarketDataTick2());
        send(createSampleMarketDataTick3());
        send(createSampleMarketDataTick4());
        send(createSampleMarketDataTick5());
        send(createSampleMarketDataTick6());
        send(createSampleMarketDataTick7());
    }
}

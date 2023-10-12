package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyStretchAlgoBackTest extends AbstractAlgoBackTest {

    private static final Logger logger = LoggerFactory.getLogger(MyStretchAlgoBackTest.class);

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyStretchAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        //create a sample market data tick....
        send(createTick3()); // 1

        send(createTick4()); // 2

        send(createTick3()); // 3

        send(createTick4()); // 4

        send(createTick3()); // 5

        send(createTick()); // 6

        send(createTick4()); // 7

        send(createTick4()); // 8

        send(createTick()); // 9

        send(createTick4()); // 10




    }

}


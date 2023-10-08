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
        send(createTick());
        createAlgoLogic();

        send(createTick2());
        createAlgoLogic();

        send(createTick3());
        createAlgoLogic();

        send(createTick4());
        createAlgoLogic();

        send(createTick3());
        createAlgoLogic();

        send(createTick4());
        createAlgoLogic();

        send(createTick());
        createAlgoLogic();

        send(createTick4());
        createAlgoLogic();
        //96
//
//        send(createTick());
//        createAlgoLogic();
//        //104
//
//        send(createTick4());
//        createAlgoLogic();
//        //96


    }

}


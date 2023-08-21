package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StretchAlgoBackTest extends AbstractStretchAlgoBackTest{

    @Override
    public AlgoLogic createAlgoLogic() {

        return new StretchAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        //create a sample market data tick....
        send(createTick());

    }

}

package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StretchAlgo2BackTest extends AbstractStretchAlgoBackTest{

    @Override
    public AlgoLogic createAlgoLogic() {
        return new StretchAlgoLogic2();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        //creating a sample market data tick....
        send(createTick());
        send(createTick2());
        send(createTick3());
        send(createTick4());

        //determine we have 3 orders created
        //assertEquals(container.getState().getChildOrders().size(), 3);
    }

}

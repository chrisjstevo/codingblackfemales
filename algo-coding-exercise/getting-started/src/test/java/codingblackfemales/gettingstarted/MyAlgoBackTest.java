package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

public class MyAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        //create a sample market data tick....
        send(createTick());

        assertEquals(container.getState().getChildOrders().size(), 3);
        
        var state = container.getState();

         long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();

        assertEquals(0, filledQuantity);
          
    }

    @Test
    public void testExampleBackTest2() throws Exception {
        send(createTick2());

        assertEquals(container.getState().getChildOrders().size(), 3);

        var state = container.getState();

         long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();

        assertEquals(3000, filledQuantity);
          
    }

    @Test
    public void testExampleBackTest3() throws Exception {
        send(createTick3());

        assertEquals(container.getState().getChildOrders().size(), 3);

        var state = container.getState();

         long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();

        assertEquals(0, filledQuantity);
          
    }

    @Test
    public void testExampleBackTest4() throws Exception {
        send(createTick4());

        assertEquals(container.getState().getChildOrders().size(), 3);

        var state = container.getState();

         long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();

        assertEquals(6000, filledQuantity);
          
    }

}

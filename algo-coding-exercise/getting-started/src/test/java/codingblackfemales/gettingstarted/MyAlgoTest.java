package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * This test is designed to check your algo behavior in isolation of the order book.
 *
 * You can tick in market data messages by creating new versions of createTick() (ex. createTick2, createTickMore etc..)
 *
 * You should then add behaviour to your algo to respond to that market data by creating or cancelling child orders.
 *
 * When you are comfortable you algo does what you expect, then you can move on to creating the MyAlgoBackTest.
 *
 */
public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        //this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }


    @Test
    public void testDispatchThroughSequencer() throws Exception {

        //create a sample market data tick.... - no orders fulfilled
        send(createTick());
        assertEquals(container.getState().getChildOrders().size(), 4);

        //market moves towards us
        send(createTick2());
        assertEquals(container.getState().getChildOrders().size(), 4);
        //cannot check filled orders because it is not connected to orderbook
        //assertEquals(container.getState().getChildOrders().stream().filter(cf -> cf.getFilledQuantity()>0).count(), 2);


        //market moves away from us
        send(createTick3());
        assertEquals(container.getState().getChildOrders().size(), 4);



    }
}

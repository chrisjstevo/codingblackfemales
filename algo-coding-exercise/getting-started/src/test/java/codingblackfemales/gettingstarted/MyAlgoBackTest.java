package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test plugs together all of the infrastructure, including the order book (which you can trade against)
 * and the market data feed.
 *
 * If your algo adds orders to the book, they will reflect in your market data coming back from the order book.
 *
 * If you cross the srpead (i.e. you BUY an order with a price which is == or > askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the childOrders of the state object.
 *
 * If you cancel the order your child order will show the order status as cancelled in the childOrders of the state object.
 *
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest {
	
	private static final Logger logger = LoggerFactory.getLogger(MyAlgoBackTest.class);
    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testExampleBackTest() throws Exception {
        //create a sample market data tick....
        send(createTick());

        //ADD asserts when you have implemented your algo logic
        logger.info("[MYALGO] Going to assert child = 6");
        assertEquals(container.getState().getChildOrders().size(), 6);
        
        
        logger.info("[MYALGO] Going to start cancelling orders");
        //when: market data moves towards us
        send(createTick2());
        assertEquals(container.getState().getActiveChildOrders().size(), 0);
        
       
        //then: get the state
//        var state = container.getState();

        //Check things like filled quantity, cancelled order count etc....
        //long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        //and: check that our algo state was updated to reflect our fills when the market data
        //assertEquals(225, filledQuantity);
//        try {
//        	var firstOrder = state.getChildOrders().stream().findFirst();
//        	System.out.println("THIS IS THE FIRST ORDER " + firstOrder);
//        } catch (Exception e){
//        	System.out.println("OOOOOOOOOOOPPPPPPPPPSSSSSSSSS");
//        }
        
    }

}

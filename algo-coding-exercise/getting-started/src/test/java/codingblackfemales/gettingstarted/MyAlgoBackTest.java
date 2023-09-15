package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.ChildOrder;

import static org.junit.Assert.assertEquals;

import codingblackfemales.sotw.OrderState;
import org.junit.Test;

import java.util.stream.Collectors;

/**
 * This test plugs together all of the infrastructure, including the order book
 * (which you can trade against)
 * and the market data feed.
 * <p>
 * If your algo adds orders to the book, they will reflect in your market data
 * coming back from the order book.
 * <p>
 * If you cross the spread (i.e. you BUY an order with a price which is == or >
 * askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the
 * childOrders of the state object.
 * <p>
 * If you cancel the order your child order will show the order status as
 * cancelled in the childOrders of the state object.
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest {
        public OrderService orderService;

        @Override
        public AlgoLogic createAlgoLogic() {
                return new MyAlgoLogic();
        }

        // This test checks if my algorithm successfully creates and cancels 6 child
        // orders.

        @Test
        public void testExampleBackTest() throws Exception {

                // create a sample market data tick....
                send(createTick());

                var myChildOrders = container.getState().getChildOrders();
                

                // simple assert to check I have 6 orders created
                assertEquals(myChildOrders.size(), 6);

                // when: market data moves towards us
                send(createTick2());

                // get filled quantity
                long filledQuantity = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
                                .reduce(Long::sum)
                                .get();
                // check that there are no fullfilled order
                assertEquals(0, filledQuantity);

                // get cancelled order
                long cancelledOrders = myChildOrders.stream()
                                .filter(order -> order.getState() == OrderState.CANCELLED).collect(Collectors.toList())
                                .size();

                // check that all 6 orders were cancelled
                assertEquals(6, cancelledOrders);

                send(createTick3());

                assertEquals(container.getState().getChildOrders().size(), 6);
                long filledQuantity2 = myChildOrders.stream().map(ChildOrder::getFilledQuantity)
                                .reduce(Long::sum)
                                .get();

                assertEquals(0, filledQuantity2);
                assertEquals(6, cancelledOrders);

        }

}

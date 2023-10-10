package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {
        // logging a statment
        logger.info("[MyAlgologic] In Algo Logic....");

        // converting the state object into a string variable as a reference to the order book
        final String book = Util.orderBookToString(state);

        // logging another statment about the Algo book
        logger.info("[MyAlgologic] Algo Sees Book as:\n" + book);

        // Retrieving the top bid level from the state object and store this in the nearTouch variable
        final BidLevel nearTouch = state.getBidAt(0);

        // number of stocks to buy at the nearTouch price
        long quantity = 75;
        long price = nearTouch.price;

        // variable to hold the value of the number of ChildOrders
        var totalOrderCount = state.getChildOrders().size();

        // make sure we have an exit condition...
        if (totalOrderCount > 20) {
            return NoAction.NoAction;
        }

        // variable to hold the value of the number of ActiveOrders
        final var activeOrders = state.getActiveChildOrders();

        if (activeOrders.size() > 3) {

            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[MyAlgoLogic] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            } else {
                return NoAction.NoAction;
            }
        } else {
            if (state.getChildOrders().size() < 3) {
                // then keep creating a new one
                logger.info("[MyAlgoLogic] Have:" + state.getChildOrders().size()
                        + " children, want 3, joining passive side of book with: " + quantity + " @ " + price);
                return new CreateChildOrder(Side.BUY, quantity, price);
            } else {
                logger.info("[MyAlgologic] Have:" + state.getChildOrders().size() + " children, want 3, done.");
                return NoAction.NoAction;

            }
        }
    }
}

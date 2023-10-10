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

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        BidLevel bestBid = state.getBidAt(0);

        if (!state.getActiveChildOrders().isEmpty()) {
            // If there are active orders, cancel the oldest order 
            Action cancelAction = new CancelChildOrder(state.getActiveChildOrders().get(0));
            logger.info("[MYALGO] Cancelling an active order.");
            return cancelAction;
        } else {
            // Creating a new child order at the best bid level if there are no active orders
            long quantity = 75; // Fixed quantity
            long price = bestBid.price;

            Action createAction = new CreateChildOrder(Side.BUY, quantity, price);
            logger.info("[MYALGO] Creating a new order for " + quantity + "@" + price);
            return createAction;
        }

    }
}

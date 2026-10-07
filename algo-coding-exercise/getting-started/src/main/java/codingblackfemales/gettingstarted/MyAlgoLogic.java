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

        // 1. (exit condition) Have I finished? -> stop
        var totalOrderCount = state.getChildOrders().size();

        logger.info("[MYALGO] New round. Orders made so far: " + totalOrderCount);

        if (totalOrderCount > 5) { // I've made more than 5 orders, so stop.
            logger.info("[MYALGO] Finished, doing nothing.");
            return NoAction.NoAction;
        }

        final var activeOrders = state.getActiveChildOrders();// *Get a list of my orders that are still live in the market, and keep it in a box called activeOrders.

        // Do I have a live order...
        if (activeOrders.size() > 0) {

            final var childOrder = activeOrders.stream().findFirst().get();
            long myPrice = childOrder.getPrice();//  my order's price
            long bestBidPrice = state.getBidAt(0).price;// the best bid right now

            if (bestBidPrice > myPrice) { // 2. has the best bid moved higher than my price?
                logger.info("[MYALGO] Best bid moved up to " + bestBidPrice + ", cancelling my order at " + myPrice);
                return new CancelChildOrder(childOrder); //...if so -> cancel
            }

            //Otherwise, my order is still at the best bid -> do nothing
            logger.info("[MYALGO] My order is still at the best bid, waiting.");
            return NoAction.NoAction;
        }

        // 4. No live order? -> create a buy at the best bid
        BidLevel level = state.getBidAt(0);
        long price = level.price;
        long quantity = level.quantity;
        logger.info("[MYALGO] No live order, creating buy for " + quantity + " @ " + price);
        return new CreateChildOrder(Side.BUY, quantity, price);
    }
}

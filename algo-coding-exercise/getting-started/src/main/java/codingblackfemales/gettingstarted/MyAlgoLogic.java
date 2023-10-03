package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[MYALGO] In Algo Logic....");

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel bidLevel = state.getBidAt(0);
        long quantity1 = 30;
        long price1 = bidLevel.price;

        // Get active orders
        List<ChildOrder> activeOrders = state.getActiveChildOrders();
        var totalOrderCount = state.getChildOrders().size();

        // Exit condition: Do not take action if there are already 5 child orders
        if (totalOrderCount > 5) {
            logger.info("Inside the NoAction clause");
            return NoAction.NoAction;
        }
        // If there are fewer than 3 child orders, create a new order on the sell side
        if (activeOrders.size() < 3) {
            int childOrderCount = activeOrders.size()+1;
            logger.info("[MYALGO] Adding order for " + quantity1 + "@" + price1);
            logger.info("[MYALGO] Have: " + activeOrders.size()  + " active orders, want 3, done.");
            logger.info("[MYALGO] Total "+ childOrderCount + " child order");
            return new CreateChildOrder(Side.SELL, quantity1, price1);
        }
        // If there are active orders more than 3, cancel the first one
        if (activeOrders.size() > 3) {
            ChildOrder childOrderToCancel = activeOrders.get(0);
            logger.info("[MYALGO] Cancelling order: " + childOrderToCancel);
            return new CancelChildOrder(childOrderToCancel);
        }
        // No action to take
        return NoAction.NoAction;
    }
}

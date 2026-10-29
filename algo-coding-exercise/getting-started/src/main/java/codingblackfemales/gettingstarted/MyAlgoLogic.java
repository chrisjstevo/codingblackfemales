package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    private static final int MAX_LIVE_ORDERS = 3;
    private static final long QUANTITY = 75;

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        // Nothing to price against if there is no bid side
        if (state.getBidLevels() == 0) {
            return NoAction.NoAction;
        }

        int totalOrders = state.getChildOrders().size();
        var activeOrders = state.getActiveChildOrders();

        // Phase 1: create orders until 3 have been created in total
        if (totalOrders < MAX_LIVE_ORDERS) {
            long price = state.getBidAt(0).price;
            logger.info("[MYALGO] Creating BUY " + QUANTITY + " @ " + price);
            return new CreateChildOrder(Side.BUY, QUANTITY, price);
        }

        // Phase 2: all 3 are live, so cancel the oldest one (this happens once)
        if (activeOrders.size() == MAX_LIVE_ORDERS) {
            var oldest = activeOrders.stream().findFirst();
            if (oldest.isPresent()) {
                logger.info("[MYALGO] Cancelling " + oldest.get());
                return new CancelChildOrder(oldest.get());
            }
        }

        // Phase 3: nothing left to do
        return NoAction.NoAction;
    }
}
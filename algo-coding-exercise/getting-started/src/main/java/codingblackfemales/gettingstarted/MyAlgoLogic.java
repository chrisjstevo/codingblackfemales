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

        var totalOrderCount = state.getChildOrders().size();
        if (totalOrderCount >= 20) {
            return NoAction.NoAction;
        }

        final var activeOrders = state.getActiveChildOrders();
        BidLevel level = state.getBidAt(0);

        if (activeOrders.size() > 0) {
            if (!activeOrders.isEmpty()) {
                var activeOrder = activeOrders.get(0);
                if (activeOrder.getPrice() == level.price) {
                    return NoAction.NoAction;
                }
                return new CancelChildOrder(activeOrder);
            }
            return new CreateChildOrder(Side.BUY, level.quantity, level.price);
        } else {
            final long price = level.price;
            final long quantity = level.quantity;
            logger.info("[MYALGO] Adding order for " + quantity + " @ " + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        }
    }
}

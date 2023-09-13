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

/**
 * This algorithm logic involves the creation and cancellation of 6 orders.
 * It seeks the second-best price in the market and initiates a buy order at
 * that price.
 * If the active buy order matches the best market price, it is automatically
 * canceled.
 */

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        /********
         *
         * Add your logic here....
         *
         */

        final BidLevel bestBid = state.getBidAt(0);

        final var activeOrders = state.getActiveChildOrders();

        long bestBidPrice = bestBid.price;

        final var option = activeOrders.stream().findFirst();
        long quantity = 200;

        long buyPrice = state.getBidAt(1).price;

        // Create 6 orders and automatically cancel them when the active price matches
        // the best market price.
        // No further action is taken once 6 orders are in the market.
        if (state.getChildOrders().size() < 6) {

            logger.info("[MyAlgoLogic] Adding order for: " + quantity + " @ " +
                    buyPrice);
            return new CreateChildOrder(Side.BUY, quantity, buyPrice);

        } else if (option.isPresent()) {

            var activeChildOrder = option.get();

            if (activeChildOrder.getPrice() == bestBidPrice) {
                logger.info("[MyAlgoLogic] Cancelling order: " + activeChildOrder);
                return new CancelChildOrder(activeChildOrder);
            } else {
                logger.info("[MyAlgoLogic]: No orders to cancel");
                return NoAction.NoAction;
            }

        } else {
            logger.info("[MyAlgoLogic] Have:" + state.getChildOrders().size() + " children, want 6, done.");
            return NoAction.NoAction;

        }

    }
}

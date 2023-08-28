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

        // checks if there are active orders
        if (activeOrders.size() > 0) {

            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var activeChildOrder = option.get();

                // if there are active orders check that the price is equals to the best bid

                if (activeChildOrder.getPrice() == bestBidPrice) {
                    // if the price is equal to the best bid leave it
                    return NoAction.NoAction;
                    // else you can cancel the order
                } else {
                    logger.info("[MyAlgoLogic] Cancelling order: " + activeChildOrder);
                    return new CancelChildOrder(activeChildOrder);

                }
            }
            // if you don't have active orders place an order
        } else {

            long quantity = 502;
            logger.info("[MyAlgoLogic] Adding order for: " + quantity + " @ " + bestBidPrice);
            return new CreateChildOrder(Side.BUY, quantity, bestBidPrice);
        }

        return NoAction.NoAction;
    }
}

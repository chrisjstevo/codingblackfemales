package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {
    private static final double BID_INCREASE_PERCENTAGE = 1;
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
        // The logic gets the highest bid and places a bid that is 1% higher than the
        // highest bid
        // but if the price is greater than 200, it cancels the order
        final BidLevel bestBid = state.getBidAt(0);

        long bidPrice = bestBid.price;
        long bidQuantity = 100;

        final var activeOrders = state.getActiveChildOrders();

        // if (activeOrders.size() > 0) {
        final var option = activeOrders.stream().findFirst();

        if (option.isPresent()) {
            var childOrder = option.get();

            if (bestBid != null) {

                long price = (long) (bestBid.price * (1 + BID_INCREASE_PERCENTAGE));
                if (price > 200) {
                    logger.info("[MyAlgoLogic] Cancelling order: " + childOrder);
                    return new CancelChildOrder(childOrder);
                }
                logger.info("[MyAlgoLogic] Adding order for" + bidQuantity + "@" + bidPrice);
                return new CreateChildOrder(Side.BUY, bidQuantity, price);
            }
        }
        // }
        return NoAction.NoAction;

    }
}

package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.CreateOrderDecoder;
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        /********
         * a bid order only goes through if the desired order quantity is not larger
         * than a given percentage of the total market volume
         */

        // DETERMINE ORDER
        // get the bid at the top of the order book & its price
        final BidLevel bestBid = state.getBidAt(0);
        long orderBidQuantity = 200;
        long orderBidPrice = bestBid.price;

        // calculate the total number of bids in order book & total volume
        long totalBidQuantity = 0;
        var totalBids = state.getBidLevels();
        for (int i = 0; i < totalBids; i++) {
            totalBidQuantity += state.getBidAt(i).quantity;
        }

        // CONSTANTS
        final int DESIRED_ACTIVE_ORDERS = 4;
        // determine the max volume an order can hold (25%)
        final double MAX_PERCENT_LIMIT = 0.25;
        long orderVolumeLimit = (long) (MAX_PERCENT_LIMIT * totalBidQuantity);

        // total number of orders and active orders
        // var totalOrderCount = state.getChildOrders().size();
        var activeOrderCount = state.getActiveChildOrders().size();

        // no more than 15 orders - exit condition
        // if (totalOrderCount < TOTAL_ORDER_LIMIT) {

        if (activeOrderCount < 4) {
            logger.info("[MYALGO] Have:" + state.getActiveChildOrders().size()
                    + " children, want 4, joining passive side of book with: " + orderBidQuantity + " @ "
                    + orderBidPrice);

            return new CreateChildOrder(Side.BUY, orderBidQuantity, orderBidPrice);
        } else if (activeOrderCount > 8) {
            final var activeOrders = state.getActiveChildOrders();
            final var option = activeOrders.stream().findFirst();

            var childOrder = option.get();
            logger.info("[MYALGO] Past Order Cancelled.");
            return new CancelChildOrder(childOrder);
        }

        return NoAction.NoAction;
    }
}

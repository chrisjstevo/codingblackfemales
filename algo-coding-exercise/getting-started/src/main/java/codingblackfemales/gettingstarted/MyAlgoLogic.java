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

import java.util.Optional;
import java.util.stream.Stream;

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
         * than x percentage of the total market volume.
         * 
         * if the desired order is larger than x percentage then oldest order is
         * cancelled.
         * 
         * if the order goes through, our desired quantity is increased for the next
         * order.
         */

        // DETERMINE ORDER DETAILS
        // get the bid at the top of the order book & its price
        final BidLevel bestBid = state.getBidAt(0);
        long orderBidPrice = bestBid.price;
        long orderBidQuantity;

        // as orders are made, increase the desired bid quantity for the next order
        if (state.getActiveChildOrders().size() > 0) {
            // find the most recent bid order
            final var activeOrders = state.getActiveChildOrders();
            final var recentOrder = findLastElement(activeOrders.stream());
            var recentChildOrder = recentOrder.get();

            // our desired quantity is 75 more than our most recent active order
            orderBidQuantity = recentChildOrder.getQuantity() + 75;

            // if no active orders, then our starting bid has a quantity of 100
        } else {
            orderBidQuantity = 100;
        }

        // DETERMINE TOTAL BID VOLUME IN MARKET
        // calculate the total number of bids in order book & total volume of bids
        long totalBidQuantity = 0;
        var totalBids = state.getBidLevels();
        for (int i = 0; i < totalBids; i++) { // iterate over all bids to get total quantity
            totalBidQuantity += state.getBidAt(i).quantity;
        }

        // CONSTANTS
        final int DESIRED_ACTIVE_ORDERS = 4;
        final int TOTAL_ORDER_LIMIT = 10; // total orders made, including those cancelled (for exit condition)
        // determine the max volume of the market an order can hold (25%)
        final double MAX_PERCENT_LIMIT = 0.25;
        long orderVolumeLimit = (long) (MAX_PERCENT_LIMIT * totalBidQuantity);

        // total number of orders and active orders
        var totalOrderCount = state.getChildOrders().size();
        var activeOrderCount = state.getActiveChildOrders().size();

        // Exit condition -> no more than 10 total orders (including cancelled)
        if (totalOrderCount < TOTAL_ORDER_LIMIT) {
            // want 4 active orders
            if (activeOrderCount < DESIRED_ACTIVE_ORDERS) {

                // order only created if the desired quantity is less than market volume limit
                if (orderBidQuantity <= orderVolumeLimit) {

                    logger.info("[MYALGO] Have:" + activeOrderCount
                            + " children, want 4, joining passive side of book with: " + orderBidQuantity + " @ "
                            + orderBidPrice);

                    return new CreateChildOrder(Side.BUY, orderBidQuantity, orderBidPrice);

                    // if the order quantity is more than market order book volume limit, then
                    // cancel the oldest order
                } else if (orderBidQuantity > orderVolumeLimit) {

                    logger.info(
                            "[MYALGO] Order rejected - Desired quantity exceeds market limit of "
                                    + (MAX_PERCENT_LIMIT * 100)
                                    + "%");
                    logger.info(
                            "[MYALGO] Cancelling oldest order...");

                    // find the oldest order
                    final var activeOrders = state.getActiveChildOrders();
                    final var oldestOrder = activeOrders.stream().findFirst();
                    var childOrder = oldestOrder.get();

                    logger.info("[MYALGO] Have:" + (state.getActiveChildOrders().size() - 1)
                            + " children, want 4");

                    // cancel the oldest order
                    return new CancelChildOrder(childOrder);

                }
            }
        }

        return NoAction.NoAction;
    }

    // find the last element in a stream
    public static <T> Optional<T> findLastElement(Stream<T> stream) {
        return stream.reduce((first, second) -> second);
    }
}
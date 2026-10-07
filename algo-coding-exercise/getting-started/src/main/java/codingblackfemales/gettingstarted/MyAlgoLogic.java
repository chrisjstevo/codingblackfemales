package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//   Creates a buy order while the best bid is at or below the limit.
//   Cancels the order if the best bid rises above the limit.

public class MyAlgoLogic implements AlgoLogic {

    // Logger allows us to see what algo is doing while it runs
    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    // Highest price willing to pay when buying
    private static final long BUY_LIMIT_PRICE = 98;

    // The number of units to buy in each child order
    // algo creates a child BUY order of 10 units at the best bid when
    // price is within limit
    private static final long ORDER_QUANTITY = 10;

    // Implement the evaluate method required by the AlgoLogic interface
    @Override
    public Action evaluate(SimpleAlgoState state) {

        // Log that the algorithm has received market data and is evaluating it.
        logger.info("[MYALGO] evaluate() method called");

        // takes the current order book from state and converts it into a readable
        // String
        final String orderBookAsString = Util.orderBookToString(state);

        // Print the current order book to the logs
        logger.info("[MYALGO] Current order book:\n" + orderBookAsString);

        // If there are no buyers/bids in order book algo cant determine best bid
        // safety check to prevent error
        if (state.getBidLevels() == 0) {
            return NoAction.NoAction;
        }

        // Bid levels are ordered from the best price to the worst price
        // retrieve best bid from Index 0
        final long bestBid = state.getBidAt(0).getPrice();

        // Log the best bid
        logger.info("[MYALGO] Best bid is: " + bestBid);

        // Check if already have an active child order
        // e.g. BUY 10 @ 98
        for (ChildOrder order : state.getActiveChildOrders()) {

            // If an order has already been filled, skip it and continue to next order
            if (order.getState() == OrderState.FILLED) {
                continue;
            }

            // If best bid has moved above maximum buying price
            // cancel existing order
            if (bestBid > BUY_LIMIT_PRICE) {

                logger.info(
                        "[MYALGO] Best bid is above limit. Cancel order: "
                                + order);

                return new CancelChildOrder(order);
            }

            // If child order is still active and best bid is within limit
            // leave the existing order unchanged
            logger.info("[MYALGO] Order is still valid. No action required.");

            return NoAction.NoAction;
        }

        // If the best bid is less than or equal to limit and no active child order
        // create a new BUY order at the current best bid
        if (bestBid <= BUY_LIMIT_PRICE) {

            logger.info(
                    "[MYALGO] Creating BUY order: "
                            + ORDER_QUANTITY
                            + " @ "
                            + bestBid);

            return new CreateChildOrder(
                    Side.BUY,
                    ORDER_QUANTITY,
                    bestBid);
        }

        // If best bid is above limit and no active order do nothing
        logger.info("[MYALGO] Best bid is above limit. No action required.");

        return NoAction.NoAction;
    }
}

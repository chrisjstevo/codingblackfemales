package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.action.CreateChildOrder;
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

        logger.info("[MYALGOLOGIC] My Algo Logic ..");
        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        // Get the top bid level from the order book.
        final BidLevel levelOfBid = state.getBidAt(0);

        // Get the current total number of child orders.
        var TotalChildOrders = state.getChildOrders().size();

        // Define a target price and quantity for the child order.
        long price = 100L;
        long quantity = levelOfBid.quantity;

    
        // Create child orders if there are less than 2 existing child orders.
        if (TotalChildOrders < 2) {
            logger.info("[My ALGO] Have" + state.getChildOrders().size() + " children, want 2, joining my algo with: "
                    + quantity + " @ " + price);
            return new CreateChildOrder(Side.SELL, quantity, price);
        }
        // Get the list of active child orders.
        final var activeOrders = state.getActiveChildOrders();
        final var option = activeOrders.stream().findFirst();

        // Cancel a child order if there are at least 2 active orders.
        if (activeOrders.size() >= 2) {
            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[MYALGOLOGIC] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            }
            else {
                // If no child orders were found for cancellation, take no action.
                return NoAction.NoAction;
            }   
        }

        else {
            // If the total child orders are less than 2, take no action.
            logger.info("[My ALGO] Have" + state.getChildOrders().size() + " children, want 2, done.");
            return NoAction.NoAction;
        }

    }
}

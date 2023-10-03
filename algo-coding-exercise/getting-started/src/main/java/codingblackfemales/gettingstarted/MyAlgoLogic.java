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

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        //logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        logger.info("[MYALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[MYALGO] Algo Sees Book as:\n" + book);

        // Check if there are active child orders in the state
        //if (!state.getActiveChildOrders().isEmpty()) {
        //    // If there are active child orders, cancel the first one
        //    logger.info("[MYALGO] Cancelling an active child order...");
        //    return new CancelChildOrder(state.getActiveChildOrders().get(0));
        //}

        //
        final BidLevel nearTouch = state.getBidAt(0);
        final AskLevel farTouch = state.getAskAt(0);
        //final double UPPER_SPREAD_TOLERANCE = 0.1;
        //final double LOWER_SPREAD_TOLERANCE = 0.01;

        // determine the spread (determine the spread difference between the best bid and best ask prices)
        double spread = ((double) (nearTouch.price - farTouch.price));

        if (spread > 1 && state.getChildOrders().size() < 3) {
            // If the spread is greater than or equal to the "upper spread tolerance", consider buying
            logger.info("[MYALGO] Spread is favorable, placing a buy order...");
            return new CreateChildOrder(Side.BUY, 100, farTouch.price); // Customise order size as needed

        } else if (spread < 0) {
            // If the spread is negative, consider cancelling the order
            logger.info("[MYALGO] Cancelling an active child order...");
            return new CancelChildOrder(state.getActiveChildOrders().get(0));
        } else if (spread < 1) {
            // If the spread is less than or equal to the "lower spread tolerance", consider selling
            logger.info("[MYALGO] Spread is tight, placing a sell order...");
            return new CreateChildOrder(Side.SELL, 100, nearTouch.price); // Customise order size as needed
        } else {
            // If the spread is in between, take no action
            logger.info("[MYALGO] No trading opportunity, waiting...");
            return NoAction.NoAction;
        }
    }
}

/*
        long quantity = 75;
        long bidPrice = nearTouch.price;

        final AskLevel farTouch = state.getAskAt(0);
        long askPrice = farTouch.price;

        //until we have three child orders....
        if (state.getChildOrders().size() < 3) {
            //then keep creating a new one
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " to get done " + quantity + " @ " + askPrice);
            return new CreateChildOrder(Side.BUY, quantity, askPrice);
        } else {
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 3, done.");
            return NoAction.NoAction;
        }
    }
}

        /*
        final double SPREAD_TOLERANCE = 0.5;


        BidLevel bidLevel = state.getBidAt(0);
        long topBidPrice = bidLevel.getPrice();
        long topBidQuantity = bidLevel.getQuantity();

        AskLevel askLevel = state.getAskAt(0);
        long topAskPrice = askLevel.getPrice();
        long topAskQuantity = bidLevel.getQuantity();

        double bidSpread = ((double) (topAskPrice - topBidPrice) /topBidPrice) * 100;
        double askSpread = ((double) (topBidPrice - topAskPrice) /topAskPrice) * 100;

        if (bidSpread > SPREAD_TOLERANCE) {
            logger.info("[MYALGO] Adding order for" + topBidQuantity + "@" + topBidPrice);
            return new CreateChildOrder(Side.BUY, topBidQuantity, topBidPrice);
        }

        else {
            final var activeOrders = state.getActiveChildOrders();
            final var option = activeOrders.stream().findFirst();
            var childOrder = option.get();
            logger.info("[MYALGO] Cancelling order:" + childOrder);
            return new CancelChildOrder(childOrder);
        }
            //final var activeOrders = state.getActiveChildOrders();
            //return activeOrders.stream().map(option ->
            //        new CancelChildOrder(option)
            //);
            //var childOrder = option.get();
            //logger.info("[MYALGO] Cancelling order:" + childOrder);
            //return new CancelChildOrder(childOrder);


        //if (askSpread > SPREAD_TOLERANCE) {
            //BidLevel bidLevel = state.getBidAt(0);
            //long price = bidLevel.price;
            //long quantity = bidLevel.quantity;
            //logger.info("[MYALGO] Adding order for" + topAskQuantity + "@" + topAskPrice);
            //return new CreateChildOrder(Side.SELL, topAskQuantity, topAskPrice);
        //}
        //else {
            //final var activeOrders = state.getActiveChildOrders();
            //final var option = activeOrders.stream().findFirst();
            //var childOrder = option.get();
            //logger.info("[MYALGO] Cancelling order:" + childOrder);
            //return new CancelChildOrder(childOrder);
        //}




        //double latestBuyPrice = state.getBidLevels();

        var totalOrderCount = state.getChildOrders().size();

        //make sure we have an exit condition...
        if (totalOrderCount > 20) {
            return NoAction.NoAction;
        }

        final var activeOrders = state.getActiveChildOrders();

        if (activeOrders.size() > 0) {

            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[ADDCANCELALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            }
            else{
                return NoAction.NoAction;
            }
        } else {
            BidLevel level = state.getBidAt(0);
            final long price = level.price;
            final long quantity = level.quantity;
            logger.info("[ADDCANCELALGO] Adding order for" + quantity + "@" + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        }

        //return NoAction.NoAction;
    }
}
*/
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

public class BuyLowAlgo implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(BuyLowAlgo.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[BUYLOWALGO] In Algo Logic....");

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[BUYLOWALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel bestBid = state.getBidAt(0);

        final var activeOrders = state.getActiveChildOrders();

        long bestBidPrice = bestBid.price;

        var activeChildOrderCount = state.getChildOrders().size();

        final var option = activeOrders.stream().findFirst();
        long quantity = 200;

        long prevBestBidPrice = 0;
        long bidPriceUpCount = 0;

        // create one order when there are no active orders in the market
        if (activeChildOrderCount < 1) {
            logger.info("[MyAlgoLogic] Adding first order for: " + quantity + " @ " + bestBidPrice);
            return new CreateChildOrder(Side.BUY, quantity, bestBidPrice);

        } else if (activeChildOrderCount < 6) {
            if (option.isPresent()) {

                var activeChildOrder = option.get();
                // check if best price has gone up twice
                if (bestBidPrice > prevBestBidPrice) {
                    bidPriceUpCount++;

                    if (bidPriceUpCount >= 2) {
                        logger.info("[BuyLowAlgo] Cancelling order: " + activeChildOrder);
                        return new CancelChildOrder(activeChildOrder);
                    }
                } else {
                    bidPriceUpCount = 0;
                    quantity = 250;

                    logger.info("[BuyLowAlgo] Adding order for: " + quantity + " @ " +
                            bestBidPrice);
                    return new CreateChildOrder(Side.BUY, quantity, bestBidPrice);
                }
                prevBestBidPrice = bestBidPrice;
            }

        } else {
            logger.info("[BuyLowAlgo] Have:" + state.getChildOrders().size() + " children, want 6, done.");
            return NoAction.NoAction;
        }

        return NoAction.NoAction;

    }
}
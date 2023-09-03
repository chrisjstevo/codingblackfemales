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

public class SellHighAlgo implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(SellHighAlgo.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[SellHIGHALGO] In Algo Logic....");

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[SellHIGHALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel bestBid = state.getBidAt(0);

        final AskLevel bestAsk = state.getAskAt(0);

        final var activeOrders = state.getActiveChildOrders();

        long bestAskPrice = bestAsk.price;

        var activeChildOrderCount = state.getChildOrders().size();

        final var option = activeOrders.stream().findFirst();
        long quantity = 200;

        long prevBestAskPrice = 0;
        long askPriceUpCount = 0;

        // create one order when there are no active orders in the market
        if (activeChildOrderCount < 1) {
            logger.info("[SellHighAlgo] Adding first order for: " + quantity + " @ " + bestAskPrice);
            return new CreateChildOrder(Side.BUY, quantity, bestAskPrice);

        } else if (activeChildOrderCount < 6) {
            if (option.isPresent()) {

                var activeChildOrder = option.get();
                // check if best price has gone up twice
                if (bestAskPrice > prevBestAskPrice) {
                    askPriceUpCount++;
                    // using 2 to determine if the currently trend is upward
                    if (askPriceUpCount >= 2) {
                        askPriceUpCount = 0;
                        quantity = 250;

                        logger.info("[SellHighAlgo] Adding order for: " + quantity + " @ " +
                                bestAskPrice);
                        return new CreateChildOrder(Side.BUY, quantity, bestAskPrice);

                    }
                } else {
                    logger.info("[SellHighAlgo] Cancelling order: " + activeChildOrder);
                    return new CancelChildOrder(activeChildOrder);
                }
                prevBestAskPrice = bestAskPrice;
            }

        } else {
            logger.info("[SellHighAlgo] Have:" + state.getChildOrders().size() + " children, want 6, done.");
            return NoAction.NoAction;
        }

        return NoAction.NoAction;

    }
}
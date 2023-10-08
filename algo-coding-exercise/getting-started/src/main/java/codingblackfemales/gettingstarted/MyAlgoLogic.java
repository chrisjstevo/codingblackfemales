package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
// import codingblackfemales.action.CancelChildOrder;
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
        logger.info("[ADDCANCELALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[ADDCANCELALGO] Algo Sees Book as:\n" + book);

        BidLevel topBidLevel = state.getBidAt(0);
        AskLevel topAskLevel = state.getAskAt(0);

        double priceThreshold = 0.1; // Adjust as needed

        double priceDifference = topAskLevel.price - topBidLevel.price;

        final var activeOrders = state.getActiveChildOrders();

        if (priceDifference > priceThreshold) {
            logger.info("[MYALGO] Buying opportunity detected. Price difference: " + priceDifference);
            long buyQuantity = 10;
            long buyPrice = topAskLevel.price; // Buy at the ask price

            if (activeOrders.size() > 0) {
            final var option = activeOrders.stream().findFirst();
            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[ADDCANCELALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            }
        }
            return new CreateChildOrder(Side.BUY, buyQuantity, buyPrice);
        }

        if (priceDifference < priceThreshold) {
            logger.info("[MYALGO] Selling opportunity detected. Price difference: " + priceDifference);
            long sellQuantity = 10;
            long sellPrice = topBidLevel.price; // Sell at the bid price

            return new CreateChildOrder(Side.SELL, sellQuantity, sellPrice);
        }
        // No trading opportunity detected, take no action
        return NoAction.NoAction;
    }

}

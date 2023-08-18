package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
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

public class BuyCheapSellHighAlgo implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(BuyCheapSellHighAlgo.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[BUYCHEAPSELLHIGHALGO] In Algo Logic....");

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[BUYCHEAPSELLHIGHALGO] The state of the order book is:\n" + orderBookAsString);

        BidLevel bid = state.getBidAt(0);
        AskLevel ask = state.getAskAt(0);

        long bidPrice = bid.price;
        long bidQuantity = bid.quantity;
        long askPrice = ask.price;
        long askQuantity = ask.quantity;

        // Check if the price has changed significantly since the last time we checked
        if (Math.abs(bidPrice - askPrice) > 0.01) {
            return NoAction.NoAction;
        }

        // Check if there is enough liquidity in the market to support our trade
        if (bidQuantity < 100 || askQuantity < 100) {
            // Do not make any trades if the liquidity is too low
            return NoAction.NoAction;
        }
        if (bidPrice < askPrice) {
            // Buy the stock
            return new CreateChildOrder(Side.BUY, askQuantity, askPrice);
        } else {
            // Sell the stock
            return new CreateChildOrder(Side.SELL, askQuantity, askPrice);
        }

    }
}
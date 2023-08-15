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

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        /********
         *
         * Add your logic here....
         *
         */
        // The objective of this challenge is to write a simple trading algo that
        // creates and cancels child orders.
        BidLevel bid = state.getBidAt(0);
        AskLevel ask = state.getAskAt(0);

        long bidPrice = bid.price;
        long bidQuantity = 100;
        long askPrice = ask.price;
        long askQuantity = ask.quantity;

        final var activeOrders = state.getActiveChildOrders();
        final var option = activeOrders.stream().findFirst();
        var childOrder = option.get();
        if (askPrice < 0.60) {
            logger.info("[MyAlgoLogic] Adding order for" + askQuantity + "@" + askPrice);
            new CreateChildOrder(Side.BUY, bidQuantity, bidPrice);

        }
        if (bidPrice > 100) {
            logger.info("[MyAlgoLogic] Adding order for" + bidQuantity + "@" + bidPrice);
            new CreateChildOrder(Side.SELL, askQuantity, askPrice);

        } else {
            logger.info("[MyAlgoLogic] Cancelling order: " + childOrder);
            new CancelChildOrder(childOrder);
        }

        return NoAction.NoAction;
    }
}

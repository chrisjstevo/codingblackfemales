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

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        /********
         *
         * Add your logic here....
         *
         */
        // The objective of this challenge is to write a simple trading algo that
        // creates and cancels child orders.
        final BidLevel bid = state.getBidAt(0);
        final AskLevel ask = state.getAskAt(0);

        long bidPrice = bid.price;
        long bidQuantity = 100;
        long askPrice = ask.price;
        long askQuantity = ask.quantity;

        final var activeOrders = state.getActiveChildOrders();
// don't hard code the price
// get the bid price
// place a bid that's slightly less than what the best bid is e.g 1%
// look at how passalfgo does it and add cancel algo does it as well
//if time has passed a
// buy cheap and sell more expensive
// sell wjen it high and buy back when it cheaper
// addcancelalgo order checks if the bid as been in the market for more than a certain time, if so it cancels 

        if (askPrice < 0.60  ) {
            logger.info("[MyAlgoLogic] Adding order for" + askQuantity + "@" + askPrice);
            return new CreateChildOrder(Side.BUY, bidQuantity, bidPrice);

        }

        if (activeOrders.size() > 0) {
            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var childOrder = option.get();
                if (bidPrice > 100) {
                    logger.info("[MyAlgoLogic] Adding order for" + bidQuantity + "@" + bidPrice);
                    return new CreateChildOrder(Side.BUY, askQuantity, askPrice);

                } else {
                    logger.info("[MyAlgoLogic] Cancelling order: " + childOrder);
                    return new CancelChildOrder(childOrder);
                }
            }
        }
        return NoAction.NoAction;

    }
}

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

        //
        BidLevel nearTouch = state.getBidAt(0);
        AskLevel farTouch = state.getAskAt(0);

        // determine the spread (difference between the best bid and best ask prices)
        double spread = ((double) (nearTouch.price - farTouch.price));


        if (spread >= 1 && state.getChildOrders().size() < 3) {
            //take as much as we can from the far touch....
            long askQuantity = farTouch.quantity;
            long askPrice = farTouch.price;
            // If the spread is favourable (greater than 1) and we have less than 3 child orders, consider buying
            logger.info("[MYALGO] Spread is favorable, placing a buy order for " + askQuantity + " @ £" + askPrice);
            return new CreateChildOrder(Side.BUY, askQuantity, askPrice);

        } else if (spread < 0 && state.getChildOrders().size() < 3) {
            //take as much as we can from the near touch....
            long bidQuantity = nearTouch.quantity;
            long bidPrice = nearTouch.price;
            // If the spread is not favourable/negative, consider selling
            logger.info("[MYALGO] Spread is not favourable, placing a sell order for " + bidQuantity + " @ £" + bidPrice);
            return new CreateChildOrder(Side.SELL, bidQuantity, bidPrice);

        } else if (spread < 1 && state.getChildOrders().size() < 3) {
            // If the spread is tight (less than 1), consider cancelling the order
            logger.info("[MYALGO] Spread is tight, Cancelling an active child order...");
            return new CancelChildOrder(state.getActiveChildOrders().get(0));

        } else {
            // If there is no trading opportunity, take no action
            logger.info("[MYALGO] No trading opportunity, waiting...");
            return NoAction.NoAction;
                }
            }
        }



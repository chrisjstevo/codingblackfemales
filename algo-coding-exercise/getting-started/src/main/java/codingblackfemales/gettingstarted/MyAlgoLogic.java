package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;

public class MyAlgoLogic implements AlgoLogic {

    /**
     * creates and cancels child orders based on the mean reversion trading strategy.
     * The mean reversion works buy low, sell high
     * - Calculate the mean price from the SELL side using the last N SELL orders or Closing Prices(if Available)
     * - Set a threshold based on
     * - If the order price is less than the (mean - threshold), BUY or create a child order
     * - If the order price is greater than the (mean + threshold), CANCEL a child order
     *
     */
    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[Mean Reversion Algo] In Algo Logic....");

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[Mean Reversion Algo] The state of the order book is:\n" + orderBookAsString);

        /** Determine the threshold for creating or canceling orders
         * - If the order price is less than the mean - threshold, BUY or create a child order
         *  - If the order price is greater than the mean + threshold, CANCEL a child order
         **/

        // Calculate the mean price from the SELL/ASK side using the last N (3) SELL orders
        double avgPrice = getMean(state, 3);
        double threshold = avgPrice * 0.01; // 1% threshold

        // Get the Best bid price from the order book
        BidLevel BestBid = state.getBidAt(0);
        long BestPrice = BestBid.price;


        // Get the Best ask quantity from the order book
        AskLevel BestAsk = state.getAskAt(0);
        long bestAskQuantity = BestAsk.quantity;


        // Check if the current best bid price is below the mean - threshold
        if (BestPrice < (avgPrice - threshold)){

            // All child orders should have qty of 100 or less,
            // and the sum of all child order quantities should be equal to the quantity at best ask
            // a / b + ((a % b == 0) ? 0 : 1);
            long noOfChildOrders = (bestAskQuantity / 100) + ((bestAskQuantity % 100 == 0) ? 0 : 1);

            //  Exit condition - when child orders == Math.ceil(currentQuantity / 100)
            var totalChildCount = state.getChildOrders().size();

            if (totalChildCount >= noOfChildOrders) {
                return NoAction;
            }

            logger.info("[Mean Reversion Algo] Creating " + noOfChildOrders + " child orders. \n" + totalChildCount + "child orders created");

            //  current Quantity keeps track of total quantity of all Ask child orders
            long currentQuantity = bestAskQuantity - (totalChildCount * 100);

            //  Create a new child order with newQty @ BestPrice
            long newQty = Math.min(100, currentQuantity);

            logger.info("[Mean Reversion Algo] Creating child order " + (totalChildCount + 1) + " : " + newQty + " @ " + BestPrice);

            return new CreateChildOrder(Side.BUY, newQty, BestPrice);
        }


        // Check if the child orders have a price above the mean + threshold
        final var activeOrders = state.getActiveChildOrders();

        if (activeOrders.size() > 0) {
            // for each active child order, if the price is above the mean + threshold, cancel the order
            for(int j = 0; j < activeOrders.size(); j++){
                var childOrder = activeOrders.get(j);

                if (childOrder.getPrice() > (avgPrice + threshold)) {
                    // Cancel child order
                    logger.info("[Mean Reversion Algo] Cancelling order:" + childOrder);
                    return new CancelChildOrder(childOrder);
                }
            }
        }

        // No action if the current price is within the threshold
        return NoAction;
    }


//    Method to calculate average of past ask prices
    public double getMean(SimpleAlgoState state, int numOfPastOrders){

        long sumOfPrices = 0;

        for(int i = 0; i < numOfPastOrders; i++){
            sumOfPrices += state.getAskAt(i).price;
        }

        double avgPrice = sumOfPrices/numOfPastOrders;

        return avgPrice;
    }

}

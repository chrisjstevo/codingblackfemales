package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static codingblackfemales.action.NoAction.NoAction;

/**
 * creates and cancels child orders based on the mean reversion trading strategy.
 * The mean reversion works buy low, sell high
 * - Calculate the mean price from the SELL side using the last N ASK orders or Closing Prices(if Available)
 * - Sets a threshold based on the average price
 * - If the best ASK price is less than the (mean - threshold), BUY or create a child order
 * - If the order price is greater than the (mean + threshold), CANCEL a child order
 *
 */
public class MeanReversionUsingBidSide implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MeanReversionUsingBidSide.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[Mean Reversion Algo] In Algo Logic....");

        var orderBookAsString = Util.orderBookToString(state);
        var childOrders = state.getActiveChildOrders();
        logger.info("[Mean Reversion Algo] The state of the order book is:\n" + orderBookAsString);

        // Determine the condition for creating or canceling orders
        // - If the best BID price is less than the (mean - threshold),
        //   Create a BID child order, i.e. Buy lower than average
        // - If the best BID price is greater than the (mean + threshold),
        //   CANCEL a child BID order and create an ASK child order, i.e. Sell higher than average


        // If we have access to closing price, use the past N prices instead
        // Calculate the mean price from the ASK side of the order book using the last N (3) SELL orders
        double avgPrice = getMean(state, Math.min(state.getAskLevels(), 3));
        double threshold = avgPrice * 0.01; // 1% threshold

        // Get the Best BID price from the order book
        BidLevel BestBid = state.getBidAt(0);
        long bestPrice = BestBid.price;

        // Hardcoding the quantity
        long bestQuantity = 500;


        long noOfChildOrders = bestQuantity / 100;

        var totalChildCount = childOrders.size();

        logger.info("[Mean Reversion Algo] Creating " + noOfChildOrders + " child orders. \n" + totalChildCount + " child orders created");

        if(childOrders.size() > 0) {
            printChildOrders(childOrders);
        }

        // Check if the current best BID price is below the mean - threshold
        // If the best BID price is less than the mean + threshold, this implies that we want
        // buying stocks at the price will be profitable. We create new BID orders to take advantage of the fact
        // that the best BID price is lower than the average price people are willing to sell the stock at
        // All child orders should have qty of 100,
        if (bestPrice < (avgPrice - threshold)){



            //  Exit condition
            //  - when number of child orders is equal to the number of active child orders
            if (totalChildCount == noOfChildOrders) {
                return NoAction;
            }


            //  current Quantity keeps track of total quantity of all Ask child orders
            //            long currentQuantity = bestQuantity - (totalChildCount * 100L);
            //  Create a new child order with newQty @ BestPrice
            //            long newQty = Math.min(100, currentQuantity);

            logger.info("[Mean Reversion Algo] Creating child order " + (totalChildCount + 1) + " : " + 100 + " @ " + bestPrice);

            return new CreateChildOrder(Side.BUY, 100, bestPrice);
        }


        // Check if the Active BID child orders have a price above the mean + threshold
        // If the child order BID prices are greater than the mean + threshold, this implies that we want
        // to buy at a price that's not profitable, instead we should cancel the orders and create ASK orders to
        // take advantage of the fact that the average price people are willing to sell the stock at has increased

        Side bidSide = Side.BUY;
        if (childOrders.size() > 0) {

            // for each UNFILLED BID child order, if the price is above the mean + threshold, cancel the order
            for (codingblackfemales.sotw.ChildOrder childOrder : childOrders) {
                if (childOrder.getSide().equals(bidSide) && childOrder.getPrice() > (avgPrice + threshold) && childOrder.getFilledQuantity() == 0) {
                    // Cancel child BID order
                    logger.info("[Mean Reversion Algo] Cancelling order:"  + childOrder + " \n"
                            + childOrder.getSide() + " Side" + "\n" + "Quantity: " + childOrder.getQuantity() + " @ " + "Price: " + childOrder.getPrice());
                    return new CancelChildOrder(childOrder);

                }
            }
            // Create a new BID child order since the best ASK price is above the (mean - threshold)
            // All child orders should have qty of 100,
            if (totalChildCount < noOfChildOrders) {
                logger.info("[Mean Reversion Algo] Creating child order " + (totalChildCount + 1) + " : " + 100 + " @ " + bestPrice);
                return new CreateChildOrder(Side.SELL, 100, bestPrice);
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

        return (double) (sumOfPrices/numOfPastOrders);
    }


    //    Method to print created child orders and filled ones
    public void printChildOrders(List<ChildOrder> childOrders){
        for (ChildOrder childOrder : childOrders) {
            System.out.println("Child Order ID: " + childOrder + " | State: " + childOrder.getState());
            System.out.println(childOrder.getSide() + " Side | Quantity: " + childOrder.getQuantity() + " @ " + "Price: " + childOrder.getPrice());

            if(childOrder.getFilledQuantity() > 0){
                System.out.println("Fill Quantity: " + childOrder.getFilledQuantity() + " @ Fill Price: " + childOrder.getPrice());
            }
            System.out.println();
        }

    }
    }





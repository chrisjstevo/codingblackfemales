package codingblackfemales.gettingstarted;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.order.Side;

/**
 * The StretchAlgoLogic class represents a trading algorithm designed to make informed trading 
 * decisions based on historical market data trends. It calculates the weighted average of
 * historical market data and evaluates trends to determine whether to buy, sell, or take no 
 * action in the market. This algorithm aims to enhance trading strategies by analyzing market 
 * conditions and making data-driven choices.
 */

public class StretchAlgoLogic implements AlgoLogic {

   private final List<Double> weightedAverageDifferences = new ArrayList<>();
   private static final Logger logger = LoggerFactory.getLogger(StretchAlgoLogic.class);
   private static final int COUNT = 3;

    @Override
    public Action evaluate(SimpleAlgoState state) {
        logger.info("[STRETCHALGO] Stretch Algo Logic....");

        // Convert order book to order levels for analysis
        List<OrderBookLevel> marketOrderLevels = convertOrderBookToOrderLevels(state);

        // Get the best bid and ask levels
        final BidLevel bestBid = state.getBidAt(0);
        final AskLevel bestAsk = state.getAskAt(0);
        final int totalOrderCount = state.getChildOrders().size();
        final long quantity = 100;

        // Check if the order count limit is exceeded
        if (totalOrderCount >= COUNT) {
            logger.info("Exceeded the order count limit.");
            return NoAction.NoAction;
        }

        // Calculate weighted average and add to the history
        double weightedAverage = calculateWeightedAverage(marketOrderLevels);
        weightedAverageDifferences.add(weightedAverage);

        // Ensure enough historical data points for analysis
        if (weightedAverageDifferences.size() < COUNT) {
            logger.info("Need more market data for calculation.");
            return NoAction.NoAction;
        }

        // Calculate the trend based on historical data
        double orderBookTrend = calculateListDifference(weightedAverageDifferences);
        System.out.println("This is the weighted average of the market:" + weightedAverageDifferences);
        System.out.println("current state of the market trend is:" + orderBookTrend);

        if (orderBookTrend > 0) {
            // Buy when the trend is positive
            return new CreateChildOrder(Side.BUY, quantity, bestBid.price);
        } else if (orderBookTrend < 0) {
            // Sell when the trend is negative
            return new CreateChildOrder(Side.SELL, quantity, bestAsk.price);
        } else {
            // Market is stable meaning the orderbooktrend is at 0, take no action
            logger.info("Market is stable.");
            return NoAction.NoAction;
        }
    }

    // Calculate the sum of differences in a list of numbers
    public double calculateListDifference(List<Double> marketprices) {
        double sumOfDifferences = 0;
        int size = marketprices.size();

        // Iterate through the list of market prices
        for (int i = 1; i < size; i++) {
            // Calculate the difference between consecutive prices
            double diff = marketprices.get(i) - marketprices.get(i - 1);
            // Accumulate the differences to calculate the sum
            sumOfDifferences += diff;
        }

        // Return the sum of differences
        return sumOfDifferences;
    }

    // Calculate the weighted average of order book levels
    public double calculateWeightedAverage(List<OrderBookLevel> marketOrders) {
        long totalQuantity = 0;
        long weightedSum = 0;

        // Iterate through the list of order book levels
        for (OrderBookLevel order : marketOrders) {
            // Accumulate the total quantity
            totalQuantity += order.getQuantity();
            // Calculate the weighted sum (price * quantity) for each order
            weightedSum += order.getPrice() * order.getQuantity();
        }

        if (totalQuantity == 0) {
            // When total quantity is 0, avoid division by zero
            return 0;
        }
        
        // Calculate the weighted average 
        return weightedSum / totalQuantity;
    }

    // Convert the order book levels in SimpleAlgoState to a list of OrderBookLevel objects
    private List<OrderBookLevel> convertOrderBookToOrderLevels(final SimpleAlgoState state) {
        List<OrderBookLevel> orderLevels = new ArrayList<>();

        // Determine the maximum number of levels (bids or asks) to process
        int maxLevels = Math.max(state.getAskLevels(), state.getBidLevels());

        // Iterate through levels
        for (int i = 0; i < maxLevels; i++) {
            // Check if there are bid levels available at the current index
            if (state.getBidLevels() > i) {
                // Retrieve bid level information
                BidLevel bidLevel = state.getBidAt(i);
                // Create an OrderBookLevel object and add it to the list
                orderLevels.add(new OrderBookLevel(bidLevel.price, bidLevel.quantity));
            }

            // Check if there are ask levels available at the current index
            if (state.getAskLevels() > i) {
                // Retrieve ask level information
                AskLevel askLevel = state.getAskAt(i);
                // Create an OrderBookLevel object and add it to the list
                orderLevels.add(new OrderBookLevel(askLevel.price, askLevel.quantity));
            }
        }

        // Return the list of OrderBookLevel objects representing order book levels
        return orderLevels;
    }

    // Class to represent an order book level
    public class OrderBookLevel {
        private final long price;
        private final long quantity;

        public OrderBookLevel(long price, long quantity) {
            this.price = price;
            this.quantity = quantity;
        }

        public long getQuantity() {
            return quantity;
        }

        public long getPrice() {
            return price;
        }
    }
}

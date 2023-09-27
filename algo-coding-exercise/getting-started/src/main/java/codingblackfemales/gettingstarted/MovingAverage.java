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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovingAverage implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MovingAverage.class);

    // First I use position to decide whether my Algo is currently holding the
    // stock(1) or not(0)
    int position = 0;

    int shortTermPeriod = 10; // Number of days used to calculate the short-term moving average
    int longTermPeriod = 50; // Number of days used to calculate the long-term moving average

    List<Long> historicalPrices = new ArrayList<>(); // Used for storing historical prices.

    List<Double> shortTermMA = new ArrayList<>(); // stores the short term moving average for each day
    List<Double> longTermMA = new ArrayList<>(); // stores the long term moving average for each day

    @Override
    public Action evaluate(SimpleAlgoState state) {

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[MOVINGAVERAGE] The state of the order book is:\n" + orderBookAsString);
        final BidLevel bid = state.getBidAt(0);
        final AskLevel ask = state.getAskAt(0);
        long buyPrice = bid.price;
        long sellPrice = ask.price;

        long quantity = 70;

        var totalOrderCount = state.getChildOrders().size();
        final long MAX_ORDER_COUNT = 5000L;

        if (totalOrderCount > MAX_ORDER_COUNT) {
            logger.info("[MOVINGAVERAGE]: " + MAX_ORDER_COUNT + " orders created");
            return NoAction.NoAction;
        }

        // Step 4: Get the bid and ask prices and add them to gatherHistoricalData()

        int maxLevels = Math.max(state.getAskLevels(), state.getBidLevels());
        for (int i = 0; i < maxLevels; ++i) {
            // getting the bid at each levels
            if (state.getBidLevels() > i) {
                // Calculate moving averages for the current tick.
                gatherHistoricalData(state.getBidAt(i).price);
            }
            // getting the ask at each levels
            if (state.getAskLevels() > i) {
                // Calculate moving averages for the current tick.
                gatherHistoricalData(state.getAskAt(i).price);
            }

            // todo - take a look at buy and sell price
            // Buy and Sell order based on shouldBuy and shouldSell logic()
            // Check buy and sell conditions.
            if (shouldBuy() && position == 0) {
                // Buy the stock.
                logger.info("[MOVINGAVERAGE]: Adding buy order for: " + quantity + " @ " + buyPrice);
                return new CreateChildOrder(Side.BUY, quantity, buyPrice);

                // Todo - do not sell at a price less than you bought the stock
            } else if (shouldSell() && position == 1) {
                // Sell the stock.
                logger.info("[MOVINGAVERAGE]: Adding sell order for: " + quantity + " @ " + sellPrice);
                return new CreateChildOrder(Side.SELL, quantity, sellPrice);
            }
        }

        logger.info("[MOVINGAVERAGE]: No orders to execute");
        return NoAction.NoAction;
    }

    // Step 1: Gather historical price data for bid and ask.

    public void gatherHistoricalData(long price) {
        // Extract the prices from the tick data and add it to historicalPrices list.
        historicalPrices.add(getPrice(price));

        // Calculate short-term moving average.
        if (historicalPrices.size() >= shortTermPeriod) {
            shortTermMA.add(calculateSMA(shortTermPeriod));

        }
        // Calculate long-term moving average.
        if (historicalPrices.size() >= longTermPeriod) {
            longTermMA.add(calculateSMA(longTermPeriod));
        }

        logger.info("[MOVINGAVERAGE]: historical prices = " + historicalPrices);
        logger.info("[MOVINGAVERAGE]: shortTermMA = " + shortTermMA);
        logger.info("[MOVINGAVERAGE]: longTermMA = " + longTermMA);
    }

    public long getPrice(long price) {
        return price;
    }

    // Step 2: Calculate the Simple Moving Average (SMA) for a given period.
    public double calculateSMA(int period) {
        long sum = historicalPrices.stream().mapToLong(Long::longValue).sum();
        return (double) sum / period;

    }

    // Step 3: Determine buy and sell conditions based on moving averages.
    // For example, if the short-term SMA crosses above the long-term SMA, we'll
    // consider buying.
    // If the short-term SMA crosses below the long-term SMA, we'll consider
    // selling.

    public boolean shouldBuy() {
        // Check if you have enough data to calculate moving averages
        if (shortTermMA.isEmpty() || longTermMA.isEmpty()) {
            return false;
        }

        // Get the most recent values of short-term and long-term moving averages
        double latestShortTermSMA = shortTermMA.get(shortTermMA.size() - 1);
        double latestLongTermSMA = longTermMA.get(longTermMA.size() - 1);

        // Check if the short-term SMA crosses above the long-term SMA (Golden Cross).
        logger.info("[Golden Cross]: latestShortTermSMA: " + latestShortTermSMA + " > latestLongTermSMA: "
                + latestLongTermSMA);
        return latestShortTermSMA > latestLongTermSMA;
    }

    public boolean shouldSell() {
        // Check if you have enough data to calculate moving averages
        if (shortTermMA.isEmpty() || longTermMA.isEmpty()) {
            return false;
        }
        // Get the most recent values of short-term and long-term moving averages
        double latestShortTermSMA = shortTermMA.get(shortTermMA.size() - 1);
        double latestLongTermSMA = longTermMA.get(longTermMA.size() - 1);

        // Check if the long-term SMA is greater than the short-term SMA (Death Cross)
        logger.info("[Death Cross]: latestLongTermSMA: " + latestLongTermSMA + " > latestShortTermSMA: "
                + latestShortTermSMA);
        return latestShortTermSMA < latestLongTermSMA;

    }

    private long buyPrice() {
        return 0L;
    }

    private long sellPrice() {
        return 0L;
    }

}
package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;

import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.order.Side;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class MovingAverage implements AlgoLogic {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(MovingAverage.class);

    //First I use position to decide whether myAlgo is currently holding the stock(1) or not(0)
    int position = 0;

    int shortTermPeriod = 10; // Number of days used to calculate the short-term moving average
    int longTermPeriod = 50;  // Number of days used to calculate the long-term moving average

    //Todo add closing prices of the stock for the day in historical prices
    List<Long> historicalPrices = new ArrayList<>(); // Used for storing historical prices.

    List<Double> shortTermMA = new ArrayList<>(); // stores the short term moving average for each day
    List<Double> longTermMA = new ArrayList<>(); // stores the long term moving average for each day

    @Override
    public Action evaluate(SimpleAlgoState state) {
        final BidLevel bid = state.getBidAt(0);
        final AskLevel ask = state.getAskAt(0);

        long quantity = 5;
        long buyPrice = bid.getPrice();
        long sellPrice = ask.getPrice();

        List<Long> ticks = state.getChildOrders().stream().map(ChildOrder::getPrice).collect(Collectors.toList());


        // Step 4: Loop through each tick data:


        // Step 6: Continue processing ticks.

        // Step 7: When you reach the end of the tick data, your trading strategy is complete.

        // Step 8: You can evaluate the strategy's performance based on your buy and sell decisions.


        for (Object tick : ticks) {
            // Calculate moving averages for the current tick.
            gatherHistoricalData((long) tick);

            // Check buy and sell conditions.
            if (shouldBuy() && position == 0) {
                // Buy the stock.
                return new CreateChildOrder(Side.BUY, quantity, buyPrice);
            } else if (shouldSell() && position == 1) {
                // Sell the stock.
                return new CreateChildOrder(Side.SELL, quantity, sellPrice);
            }
        }


        return NoAction.NoAction;
    }


    // Step 1: Gather historical price data for a specific stock.
    // This data should include the closing prices for each day.

    public void gatherHistoricalData(long price) {
        // Extract the closing price from the tick data and add it to historicalPrices list.
        historicalPrices.add(getClosingPrice(price));

        // Calculate short-term moving average.
        if (historicalPrices.size() >= shortTermPeriod) {
            shortTermMA.add(calculateSMA(shortTermPeriod));

        }
        // Calculate long-term moving average.
        if (historicalPrices.size() >= longTermPeriod) {
            longTermMA.add(calculateSMA(longTermPeriod));
        }
    }

    public long getClosingPrice(long price) {
        // get the last trade tick
        return price;
    }

    // Step 2: Calculate the Simple Moving Average (SMA) for a given period.
    public double calculateSMA(int period) {
        // Calculate SMA for the last 'period' days in historicalPrices.
        long sum = historicalPrices.stream().mapToLong(Long::longValue).sum();
        return (double) sum / period;

    }

    // Step 3: Determine buy and sell conditions based on moving averages.
    // For example, if the short-term SMA crosses above the long-term SMA, we'll consider buying.
    // If the short-term SMA crosses below the long-term SMA, we'll consider selling.

    public boolean shouldBuy() {
        // Check if you have enough data to calculate moving averages
        if (shortTermMA.isEmpty() || longTermMA.isEmpty()) {
            return false;
        }

        // Get the most recent values of short-term and long-term moving averages
        double latestShortTermSMA = shortTermMA.get(shortTermMA.size() - 1);
        double latestLongTermSMA = longTermMA.get(longTermMA.size() - 1);

        // Check if the short-term SMA crosses above the long-term SMA (Golden Cross).

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
        return latestLongTermSMA > latestShortTermSMA;

    }

}
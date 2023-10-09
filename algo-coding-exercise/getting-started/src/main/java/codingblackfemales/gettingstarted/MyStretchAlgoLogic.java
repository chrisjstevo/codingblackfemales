package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/********
 * LOGIC
 * The following algorithm has been developed based on percentage change of price.
 * The aim is to examine the price movement of the stock using the percentage change values.
 * To express each market data update as one entity that can be monitored, I chose to find the average price of all levels in the tick. This follows the idea that if the market moves down, the average price of all levels in the tick will be of a lower value. The percentage change from tick to tick will be calculated.
 * An average percentage change value will be calculated, following x number of market data updates (algo builds a history of interactions with the market). This number can be amended to reflect 1 week's worth of updates, 1 month's worth, 6 months etc.
 * Once an average percentage change is established, the percent change values of subsequent market data updates will be calculated and compared to the most recent average.
 * If the percentage change is significantly higher than the average, the algo will take advantage of this jump and trigger a buy or sell (depending on whether the change was + or -). All other cases signify normal fluctuations in the market.
 * This average percent change will be updated with each market data update, negating any significant jumps that trigger a buy or sell (leads to a skewed value for the av % change).
 * Other conditions used to determine a sell or buy: check the quantity of stock available to sell; if buying, we have unlimited money; if buying, is the farTouch price lower than the weighted average price of bought stocks; if selling, is the stock price higher than the weighted average price of bought stocks.
 */

public class MyStretchAlgoLogic implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(MyStretchAlgoLogic.class);

    LinkedList<Long> averageTickPrices = new LinkedList<Long>();
    LinkedList<Double> percentChangeOfTicks = new LinkedList<Double>();

    //price:quantity key:value pair
    HashMap<Long, Long> boughtStocks = new HashMap<Long, Long>();
    //price:quantity key:value pair
    HashMap<Long, Long> soldStocks = new HashMap<Long, Long>();

    double averagePercentChange;
    long currentAverageTickPrice;
    long previousTickAverage;
    double percentageChange;

    int tickIndex = 0;




    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        final AskLevel farTouch = state.getAskAt(0);

        tickIndex+=1; // for tracking purposes

        logger.info("[MYSTRETCHALGO] The state of the order book is:\n" + orderBookAsString);
        logger.info("[MYSTRETCHALGO] Market Data Update: " + tickIndex);




        if (averageTickPrices.isEmpty()) {
            currentAverageTickPrice = calculateAverageTickPrice(state);
            storeAverageTickPrice(currentAverageTickPrice);

        } else if (averageTickPrices.size() <5) { // Find the percent change of tick prices for a few runs and store. This allows us to build up enough data for a reliable avg percent change.
            previousTickAverage = averageTickPrices.peekLast();
            currentAverageTickPrice = calculateAverageTickPrice(state);
            storeAverageTickPrice(currentAverageTickPrice);

            percentageChange = calculatePercentChange(previousTickAverage,currentAverageTickPrice);
            percentChangeOfTicks.add(percentageChange);

            logger.info("[MYSTRETCHALGO] The most recent percent change of average tick prices are: " + percentChangeOfTicks.toString());

        } else { // Buy or Sell Logic
            //Calculate new average tick price & latest percentage change
            calculateAveragePercentChange(); // calculate average percent change of currently held data. Stores in averagePercentChange

            previousTickAverage = averageTickPrices.peekLast();
            currentAverageTickPrice = calculateAverageTickPrice(state);
            logger.info("Current average tick price is: " + currentAverageTickPrice);
            logger.info("[MYSTRETCHALGO] Log of most recent average tick prices: " + averageTickPrices);

            percentageChange = calculatePercentChange(previousTickAverage,currentAverageTickPrice);
            logger.info("[MYSTRETCHALGO] Log of most recent percent change values are: " + percentChangeOfTicks.toString());


            //Check if latest percent change  is out of bounds for limit (set as 1.5 x average percent change). Do not store percentage change - will skew average percentage change value.
            if((Math.abs(percentageChange)>averagePercentChange) && (percentageChange > 0)) {
                //sell
                logger.info("[MYSTRETCHALGO] Current average percent change is: " + averagePercentChange);
                logger.info("[MYSTRETCHALGO] Latest percent change is " + percentageChange + " which is a positive value and exceeds the current average percent change. Sell now.");

                storeAverageTickPrice(currentAverageTickPrice); // should this value be stored?

                long price = farTouch.price;
                //Add more conditions?
                if (price > calculateWeightedAveragePriceOfBoughtStocks() && calculateTotalQuantityOfBoughtStocks()!=0){
                    return new CreateChildOrder(Side.SELL,calculateTotalQuantityOfBoughtStocks(),price);
                } else if (price <= calculateWeightedAveragePriceOfBoughtStocks()) {
                    logger.info("The price of the stock is not greater than the weighted average of already purchased stocks.");
                }

            } else if ((Math.abs(percentageChange)>averagePercentChange) && (percentageChange < 0)) {
                // buy - assuming we have unlimited money.
                logger.info("[MYSTRETCHALGO] Current average percent change is: " + averagePercentChange);
                logger.info("[MYSTRETCHALGO] latest percent change is " + percentageChange + " which is a negative value and exceeds the current average percent change. Buy now.");
                storeAverageTickPrice(currentAverageTickPrice);
                //percentChangeOfTicks.add(percentageChange);

                // Should i add the average tick price to the list?
                //Will not be adding percentage change - will skew the data
                long quantity = farTouch.quantity;
                long price = farTouch.price;
                boughtStocks.put(price, quantity);
                logger.info("Here are all of the purchased stocks and their associated prices: " + boughtStocks);
                return new CreateChildOrder(Side.BUY, quantity, price);

            } else {
                logger.info("[MYSTRETCHALGO] The latest percentage change is " + percentageChange + " which is within bounds. Do Not Buy or Sell");
                storeAverageTickPrice(currentAverageTickPrice);
                percentChangeOfTicks.add(percentageChange);
            }

        }
        return NoAction.NoAction;
        }





    public long calculateAverageTickPrice(SimpleAlgoState state) {
        long askLevels = state.getAskLevels();
        long bidLevels = state.getBidLevels();

        long askPrices = 0L;
        //iterate over ask side
        for (int i=0; i < askLevels; i++) {
            long price = state.getAskAt(i).getPrice();
            //logger.info("Price at index " + i + " is " + price);
            askPrices += price;
        }

        long bidPrices = 0L;
        //iterate over bid side
        for (int i=0; i < bidLevels; i++) {
            long price = state.getBidAt(i).getPrice();
            //logger.info("Price at index " + i + " is " + price);
            bidPrices += price;
        }

        return (askPrices+bidPrices)/(askLevels+bidLevels);
    }



    public double calculatePercentChange(long averageTickPrice1, long averageTickPrice2) {
        double avgTickPrice1 = averageTickPrice1;
        double avgTickPrice2 = averageTickPrice2;
        return (avgTickPrice2 -avgTickPrice1)/(avgTickPrice1)*100;
    }



    public void calculateAveragePercentChange() {
        double sumOfPercentChange = 0;
        for (Double percentChangeOfTick : percentChangeOfTicks) {
            sumOfPercentChange += Math.abs(percentChangeOfTick);
        }

        averagePercentChange = sumOfPercentChange/percentChangeOfTicks.size();
    }



    public void storeAverageTickPrice(long averageTickPrice) {
        // Can decide the size of retained data. Could be a week's worth of ticks or 6 month's worth of ticks. Allows to follow trends as they emerge?
        if (averageTickPrices.size() <5) {
            averageTickPrices.add(averageTickPrice);
            logger.info("[MYSTRETCHALGO] storeAverageTickPrice: " + averageTickPrices);
        } else {
            averageTickPrices.remove();
            averageTickPrices.add(averageTickPrice);
            logger.info("[MYSTRETCHALGO] storeAverageTickPrice: " + averageTickPrices);
        }

    }


    public long calculateTotalQuantityOfBoughtStocks() {
        long sumOfQuantities = 0;
        for (long quantity:boughtStocks.values()) {
            sumOfQuantities += quantity;
        }
        logger.info("The total quantity of bought stocks is " + sumOfQuantities);
        return sumOfQuantities;
    }


    public long calculateWeightedAveragePriceOfBoughtStocks() {
        if (boughtStocks.isEmpty()) {
            return 0;
        }

        long sumOfProducts = 0;
        long sumOfQuantities = 0;
        for(Map.Entry<Long, Long> e: boughtStocks.entrySet()) {
            long price = e.getKey();
            long quantity = e.getValue();

            long product = price*quantity;

            sumOfQuantities += quantity;
            sumOfProducts+= product;
        }

        logger.info("Weighted average of purchased stocks is sum of products (" + sumOfProducts + ") divided by the sum of quantities (" + sumOfQuantities + "). Equalling: " + sumOfProducts/sumOfQuantities);
        return sumOfProducts/sumOfQuantities;

    }


}

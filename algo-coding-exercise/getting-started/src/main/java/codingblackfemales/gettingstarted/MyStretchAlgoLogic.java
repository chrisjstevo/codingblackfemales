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

public class MyStretchAlgoLogic implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(MyStretchAlgoLogic.class);

    /********
     * Add your logic here....
     * As soon as tick is available update data structure with new tick value.
     */

    LinkedList<Long> averageTickPrices = new LinkedList<Long>();
    LinkedList<Double> percentChangeOfTicks = new LinkedList<Double>();

    //price:quantity key:value pair
    HashMap<Long, Long> boughtStocks = new HashMap<Long, Long>();
    //price:quantity key:value pair
    HashMap<Long, Long> soldStocks = new HashMap<Long, Long>();

    double averagePercentChange;
    long currentAverageTickPrice;
    long previousTickAverage;

    int tickIndex = 0;




    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYSTRETCHALGO] The state of the order book is:\n" + orderBookAsString);

        final AskLevel farTouch = state.getAskAt(0);

        tickIndex+=1;
        logger.info("This is for Tick " + tickIndex);


        if (averageTickPrices.isEmpty()) {
            currentAverageTickPrice = calculateAverageTickPrice(state);
            storeAverageTickPrice(currentAverageTickPrice);

        } else if (averageTickPrices.size() <5) { // Find the percent change of tick prices for a few runs and store. This allows us to build up enough data for a reliable avg percent change.
            previousTickAverage = averageTickPrices.peekLast();
            currentAverageTickPrice = calculateAverageTickPrice(state);
            storeAverageTickPrice(currentAverageTickPrice);

            double percentageChange = calculatePercentChange(previousTickAverage,currentAverageTickPrice);
            percentChangeOfTicks.add(percentageChange);
            logger.info("[MYSTRETCHALGO] The most recent percent change of average tick prices are: " + percentChangeOfTicks.toString());

        } else { // Buy or Sell Logic
            //Calculate new average tick price & latest percentage change
            calculateAveragePercentChange(); // calculate average percent change of currently held data. Stores in averagePercentChange

            previousTickAverage = averageTickPrices.peekLast();
            currentAverageTickPrice = calculateAverageTickPrice(state);
            logger.info("Current average tick price is: " + currentAverageTickPrice);
            logger.info("[MYSTRETCHALGO] storeAverageTickPrice: " + averageTickPrices);

            double percentageChange = calculatePercentChange(previousTickAverage,currentAverageTickPrice);
            logger.info("[MYSTRETCHALGO] The most recent percent change of average tick prices are: " + percentChangeOfTicks.toString());


            //Check if latest percent change  is out of bounds for limit (set as 1.5 x average percent change).
            if((Math.abs(percentageChange)>averagePercentChange) && (percentageChange > 0)) {
                //sell
                logger.info("[MYSTRETCHALGO] Current average percent change is: " + averagePercentChange);
                logger.info("[MYSTRETCHALGO] Latest percent change is " + percentageChange + " which is a positive value and exceeds the current average percent change. Sell now.");

                //percentageChange = calculatePercentChange(previousTickAverage,currentAverageTickPrice);
                //percentChangeOfTicks.add(percentageChange);
                storeAverageTickPrice(currentAverageTickPrice);
                //percentChangeOfTicks.add(percentageChange);

                long price = farTouch.price;

                if (price > calculateWeightedAveragePriceOfBoughtStocks() && calculateTotalQuantityOfBoughtStocks()!=0){
                    return new CreateChildOrder(Side.SELL,calculateTotalQuantityOfBoughtStocks(),price);
                }

                //should i add the average tick price to the list?
                //Will not be adding percentage change - will skew the data

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
        logger.info("[MYSTRETCHALGO] Updated averagePercentChange " + averagePercentChange);

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
        long sumOfProducts = 0;
        for(Map.Entry<Long, Long> e: boughtStocks.entrySet()) {
            long product = e.getKey()*e.getValue();
            sumOfProducts+= product;
        }
        logger.info("Weighted average of purchased stocks is " + sumOfProducts/boughtStocks.size());
        return sumOfProducts/boughtStocks.size();
    }


}

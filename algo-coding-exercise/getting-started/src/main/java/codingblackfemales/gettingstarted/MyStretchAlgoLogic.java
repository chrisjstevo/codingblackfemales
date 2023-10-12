package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
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
 * An average percentage change value will be calculated, following x number of market data updates (algo builds an understanding of the trend of fluctuations in market value of the instrument). This number can be amended to reflect 1 week's worth of updates, 1 month's worth, 6 months etc.
 * Once an average percentage change is established, the percent change values of subsequent market data updates will be calculated and compared to the most recent average.
 * If the percentage change is significantly higher than the average (1.5x), the algo will take advantage of this jump and trigger a buy or sell (depending on whether the change was + or -). All other cases signify normal fluctuations in the market.
 * This average percent change will be updated with each market data update, negating any significant jumps that trigger a buy or sell (leads to a skewed value for the av % change).
 * Other conditions used to determine a sell or buy: check the quantity of stock available to sell; if buying, we have unlimited money; if buying, is the farTouch price lower than the weighted average price of bought stocks; if selling, is the stock price higher than the weighted average price of bought stocks.
 */


public class MyStretchAlgoLogic implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(MyStretchAlgorithmLogic.class);

    LinkedList<Double> touchlineAveragePricesList = new LinkedList<Double>();
    LinkedList<Double> touchlinePricePercentChangeList = new LinkedList<Double>();

    double latestTouchlineAveragePrice;
    double previousTouchlineAveragePrice;

    double touchlinePricePercentChange;
    double averagePercentChange;

    //price:quantity key:value pairs
    HashMap<Double, Double> purchasedStocks = new HashMap<Double, Double>();
    HashMap<Double, Double> soldStocks = new HashMap<Double, Double>();

    int tickIndex = 0; //monitoring purposes


    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYSTRETCHALGORITHM] The state of the order book is:\n" + orderBookAsString);


        // for tracking purposes
        tickIndex+=1;
        logger.info("[MYSTRETCHALGORITHM] Market Data Update: " + tickIndex);

        if (touchlineAveragePricesList.isEmpty()) {
            latestTouchlineAveragePrice = calculateTouchlineAveragePrice(state);
            storeTouchlineAveragePrice(latestTouchlineAveragePrice);
            logger.info("[MYSTRETCHALGO] The average price of the touchline is: " + latestTouchlineAveragePrice + ".This has now been added to the list of touchline average prices: " + touchlineAveragePricesList);

        } else if (touchlineAveragePricesList.size() < 5){
            previousTouchlineAveragePrice = touchlineAveragePricesList.peekLast();
            latestTouchlineAveragePrice = calculateTouchlineAveragePrice(state);
            storeTouchlineAveragePrice(latestTouchlineAveragePrice);


            logger.info("[MYSTRETCHALGO] The average price of the touchline is: " + latestTouchlineAveragePrice + ".This has now been added to the list of touchline average prices: " + touchlineAveragePricesList + "and will be compared to the previous average price " + previousTouchlineAveragePrice);


            touchlinePricePercentChange = calculatePercentChange(previousTouchlineAveragePrice, latestTouchlineAveragePrice);
            storeTouchlinePercentageChange(touchlinePricePercentChange);

            logger.info("[MYSTRETCHALGO] The most recent percent change of touchline price has been added to the list of percent changes: " + touchlinePricePercentChangeList);
        } else {
            averagePercentChange = calculateAveragePercentChangeOfTouchlinePrice();

            previousTouchlineAveragePrice = touchlineAveragePricesList.peekLast();
            latestTouchlineAveragePrice = calculateTouchlineAveragePrice(state);

            logger.info("[MYSTRETCHALGO] The average price of the touchline is: " + latestTouchlineAveragePrice + ". The latest average (" + latestTouchlineAveragePrice + ") will now be compared to the previous average (" + previousTouchlineAveragePrice + ").");
            storeTouchlineAveragePrice(latestTouchlineAveragePrice);


            touchlinePricePercentChange = calculatePercentChange(previousTouchlineAveragePrice, latestTouchlineAveragePrice);

            logger.info("[MYSTRETCHALGO] The average percent change is " + averagePercentChange + ". This will now be compared to the latest percentage change." + touchlinePricePercentChange);

            // choosing the limit to be 1.5*average price
            if ((Math.abs(touchlinePricePercentChange) > (averagePercentChange*1.5) && touchlinePricePercentChange<0)) {
                logger.info("Buy");
                long farTouchPrice = state.getAskAt(0).getPrice();
                long farTouchQuantity = state.getAskAt(0).getQuantity();

                logger.info("The calculate VWAP of held stocks is " + calculateVWAPOfHeldStocks() + " and the far touch price is " + farTouchPrice);

                if (calculateVWAPOfHeldStocks()==0 || calculateVWAPOfHeldStocks() > farTouchPrice) {
                    // order should always execute so store in purchased stocks hashmap
                    purchasedStocks.put((double) farTouchPrice, (double) farTouchQuantity);
                    return new CreateChildOrder(Side.BUY, farTouchQuantity, farTouchPrice);
                } else {
                    logger.info("Stop buy.");
                }

            } else if ((Math.abs(touchlinePricePercentChange) > (averagePercentChange*1.5) && touchlinePricePercentChange>0)) {
                logger.info("Sell");
                storeTouchlineAveragePrice(latestTouchlineAveragePrice);

                logger.info("Here are all of the purchased stocks and their associated prices that are available to be sold: " + purchasedStocks);

                long farTouchPrice = state.getBidAt(0).getPrice();
                long farTouchQuantity = state.getBidAt(0).getQuantity();
                logger.info("Far Touch Quantity [SELL]: " + farTouchQuantity);


                long quantity = Math.min((long) calculateTotalQuantityOfHeldStocks(), farTouchQuantity);

                if (calculateVWAPOfHeldStocks()!=0) {
                    // update sold stock and sell
                    soldStocks.put((double)farTouchPrice, (double)quantity);
                    return new CreateChildOrder(Side.SELL, quantity,farTouchPrice);
                } else {
                    logger.info("No stocks available to be sold");
                }

            } else {
                logger.info("[MYSTRETCHALGO] The latest percentage change is " + touchlinePricePercentChange + " which is within bounds. Do Not Buy or Sell");
                storeTouchlineAveragePrice(latestTouchlineAveragePrice);
                storeTouchlinePercentageChange(touchlinePricePercentChange);
            }

        }

        return NoAction.NoAction;
    }


    // Calculate the average of the bid and ask prices at the touchline.
    public double calculateTouchlineAveragePrice(SimpleAlgoState state) {

        double askPrice = state.getAskAt(0).getPrice();
        double bidPrice = state.getBidAt(0).getPrice();

        return (askPrice+bidPrice)/2;
    }


    // Used to calculate the percent change from one touchline average to the next.
    public double calculatePercentChange(double averageTickPrice1, double averageTickPrice2) {
        return (averageTickPrice2 - averageTickPrice1)/(averageTickPrice1)*100;
    }

    public void storeTouchlineAveragePrice(double touchlineAveragePrice) {

        if (touchlineAveragePricesList.size() <5) {
            touchlineAveragePricesList.add(touchlineAveragePrice);

        } else {
            touchlineAveragePricesList.remove();
            touchlineAveragePricesList.add(touchlineAveragePrice);
        }

    }


    // Used to identify a trend in the fluctuation of touchline price average. Each percentage change will be compared to this identified trend to determine whether to buy or sell.
    public double calculateAveragePercentChangeOfTouchlinePrice() {
        double sumOfPercentChange = 0;
        for (Double percentChange:touchlinePricePercentChangeList) {
            sumOfPercentChange += Math.abs(percentChange);
        }

        return sumOfPercentChange/touchlinePricePercentChangeList.size();
    }


    // Condition for buying and selling using VWAP
    public double calculateVWAPOfHeldStocks() {
        //VWAP of purchased stocks
        if (purchasedStocks.isEmpty()) {
            return 0;
        }
        double sumOfProducts = 0;
        double sumOfQuantities = 0;
        for(Map.Entry<Double, Double> e: purchasedStocks.entrySet()) {
            double price = e.getKey();
            double quantity = e.getValue();

            double product = price*quantity;

            sumOfQuantities += quantity;
            sumOfProducts+= product;
        }

        //VWAP of sold stocks
        for(Map.Entry<Double,Double> e: soldStocks.entrySet()) {
            double price = e.getKey();
            double quantity = e.getValue();

            double product = price*quantity;

            sumOfQuantities-=product;
            sumOfProducts -= product;
        }
        return sumOfProducts/sumOfQuantities;
    }

    public double calculateTotalQuantityOfHeldStocks() {
        double sumOfPurchasedStocks = 0;
        //bought stocks
        for (double quantity:purchasedStocks.values()) {
            sumOfPurchasedStocks += quantity;
        }

        double sumOfSoldStocks = 0;
        for (double quantity: soldStocks.values()) {
            sumOfSoldStocks+=quantity;
        }

        //find quantity of sold and minus
        logger.info("The total quantity of held stocks is " + (sumOfPurchasedStocks-sumOfSoldStocks));
        return sumOfPurchasedStocks-sumOfSoldStocks;
    }

    // Algorithm only ever holds us the 5 latest percent changes that will be used to calculate the average percent change.
    public void storeTouchlinePercentageChange(double PricePercentChange) {

        if (touchlinePricePercentChangeList .size() <5) {
            touchlinePricePercentChangeList.add(PricePercentChange);

        } else {
            touchlinePricePercentChangeList.remove();
            touchlinePricePercentChangeList.add(PricePercentChange);

        }

    }
}
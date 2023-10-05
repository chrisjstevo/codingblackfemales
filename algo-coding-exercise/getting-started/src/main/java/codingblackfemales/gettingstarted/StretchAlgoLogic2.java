package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StretchAlgoLogic2 implements AlgoLogic {

    List <Long> bidPriceList= new ArrayList<>();
    List <Long> askPriceList= new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(StretchAlgoLogic.class);
    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[STRETCH ALGO 2] In StretchAlgo Logic 2....");

        final BidLevel bidLevel = state.getBidAt(0);
        final AskLevel askLevel = state.getAskAt(0);
        var totalOrderCount = state.getChildOrders().size();
        long buyQuantity = 50;
        long askQuantity = 50;

        //get sum of difference in bid list
        //put numbers in list

        //get sum of differences in ask list

        //implement buy or sell logic

        //if sum is positive then buy
        //else if negative sell

        //logger.info(totalOrderCount + " is totalOrderCount");

        //exit condition
        if(totalOrderCount >= 3){
            logger.info("Total Order Count is greater than or equal to 3 and has passed the limit");
            return NoAction.NoAction;
        }

        double movingBidAverage = calculateMovingBidAverage(state);
        double movingAskAverage = calculateMovingAskAverage(state);

        logger.info("Moving Bid Average is " + movingBidAverage);
        logger.info("Moving Ask Average is " + movingAskAverage);


        if (bidPriceList.size() < 3 || askPriceList.size() < 3) {
            // Return NoAction if the lists don't have enough prices
            logger.info("Not enough prices in bidPriceList or askPriceList. Waiting for more data...");
            return NoAction.NoAction;
        }

        // Compare current bid and ask prices with averages
        //implement passive logic to buy or sell
        if (calculateListDifference(bidPriceList) > 0) {
            // Buy when the current bid price is cheaper than the average
            logger.info("Buying: Current bid price is positive");
            return new CreateChildOrder(Side.BUY, buyQuantity, bidLevel.price);
        } else if (calculateListDifference(askPriceList) < 0) {
            // Sell when the current ask price is more expensive than the average
            logger.info("Selling: Current ask price negative");
            return new CreateChildOrder(Side.SELL, askQuantity, askLevel.price);
        }

        return NoAction.NoAction;
    }

    public double calculateMovingBidAverage(SimpleAlgoState state){
        // Implementing calculation based on order book
        final BidLevel bidLevel = state.getBidAt(0);
        long bidPrice = bidLevel.price;
        double sumOfList = 0;

        //when a new child order is created
        //the number gets added to the list even though it was added before
        //after adding, the order book gets updated and the same number is in index 0
        bidPriceList.add(bidPrice);
        System.out.println(bidPriceList);
        double size = bidPriceList.size();

        for (double prices : bidPriceList)
        {
            sumOfList += prices;
        }
        sumOfList = sumOfList/size;
        return sumOfList;
    }

    public double calculateMovingAskAverage(SimpleAlgoState state){
        // Implementing calculation based on order book
        final AskLevel askLevel = state.getAskAt(0);

        long askPrice = askLevel.price;

        double sumOfList = 0;

        askPriceList.add(askPrice);
        System.out.println(askPriceList);
        double size = askPriceList.size();

        for (double prices : askPriceList){
            sumOfList += prices;
        }

        sumOfList = sumOfList/size;
        return sumOfList;
    }

    //create method to calculate difference in list of diff size
    public double calculateListDifference(List<Long> prices){

        // Initialize variable to hold sum
        double sumOfList = 0;
        // Get size of the list
        int size = prices.size();

        // Iterate through the list to calculate differences
        for (int i = 1; i < size; i++) {
            long diff = prices.get(i) - prices.get(i - 1);
            sumOfList += Math.abs(diff); // Calculate absolute difference and add to sum
        }

        // Return the sum of differences
        return sumOfList;
    }
}

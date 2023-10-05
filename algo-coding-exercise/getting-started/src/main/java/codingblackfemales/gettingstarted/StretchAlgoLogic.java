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

public class StretchAlgoLogic implements AlgoLogic {

    List <Double> weightedAverageDifference= new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(StretchAlgoLogic.class);
    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[STRETCH ALGO] In Stretch Algo Logic....");

        List <MarketOrders> marketOrders = marketOrdersList(state);

        var totalOrderCount = state.getChildOrders().size();
        final BidLevel bidLevel = state.getBidAt(0);
        final AskLevel askLevel = state.getAskAt(0);
        long orderQuantity = 100;
        final double TREND_THRESHOLD = 0.0;
        final int MINIMUM_DATA_POINTS = 4;
        final int MAX_ORDERS = 4;

        if(totalOrderCount >= MAX_ORDERS){
            logger.info("Total Order Count is greater than or equal to 5 and has reached the limit");
            return NoAction.NoAction;
        }

        double weightedAverage = calculateWeightedAverage(marketOrders);

        //store each weighted average in a new list
        weightedAverageDifference.add(weightedAverage);
        //calculate the difference in the weighted average list

        // do not calculate the diff until we have a certain amount of differences inside
        if (weightedAverageDifference.size() < MINIMUM_DATA_POINTS) {
            // Return NoAction if the lists don't have enough prices
            logger.info("Not enough prices to calculate. Waiting for more data...");
            return NoAction.NoAction;
        }

        System.out.println("List of weighted average difference " + weightedAverageDifference);

        double orderBookTrend = calculateListDifference(weightedAverageDifference);
        System.out.println("Order Book Trend - " + orderBookTrend);

        //if positive buy
        if(orderBookTrend > TREND_THRESHOLD){
            //buy
            logger.info("Creating buy orders as trend is positive");
            return new CreateChildOrder(Side.BUY, orderQuantity, bidLevel.price);
        }
        //if negative sell
        else if(orderBookTrend < TREND_THRESHOLD){
            //sell
            logger.info("Creating sell orders as trend is negative");
            return new CreateChildOrder(Side.SELL, orderQuantity, askLevel.price);
        }
        //do nothing, market is stable
        else{
            logger.info("Trend is stable, doing nothing");
            return NoAction.NoAction;
        }
    }

    //method to get and store market data at each state change
    public List<MarketOrders> marketOrdersList(final SimpleAlgoState state) {
        List<MarketOrders> marketOrders = new ArrayList<>();

        int maxLevels = Math.max(state.getAskLevels(), state.getBidLevels());

        for (int i = 0; i < maxLevels; i++) {
            if (state.getBidLevels() > i) {
                BidLevel bidLevel = state.getBidAt(i);
                marketOrders.add(new MarketOrders(bidLevel.price, bidLevel.quantity));
            }

            if (state.getAskLevels() > i) {
                AskLevel askLevel = state.getAskAt(i);
                marketOrders.add(new MarketOrders(askLevel.price, askLevel.quantity));
            }
        }
        return marketOrders;
    }

    public class MarketOrders {
        private final long price;
        private final long quantity;

        public MarketOrders(long price, long quantity) {
            this.price = price;
            this.quantity = quantity;
        }

        public long getPrice() {
            return price;
        }

        public long getQuantity() {
            return quantity;
        }
    }

    public double calculateWeightedAverage(List<MarketOrders> marketOrders) {
        long totalQuantity = 0;
        long weightedSum = 0;

        for (MarketOrders order : marketOrders) {
            totalQuantity += order.getQuantity();
            weightedSum += order.getPrice() * order.getQuantity();
        }

        if (totalQuantity == 0) {
            // Handle the case where totalQuantity is zero to avoid division by zero
            return 0.0;
        }

        double weightedAverage = (double) weightedSum / totalQuantity;
        // Round the weighted average to 2 decimal points
        return Math.round(weightedAverage * 100.0) / 100.0;
    }

    public double calculateListDifference(List<Double> prices){
        double sumOfList = 0;
        int size = prices.size();

        // Iterate through the list to calculate differences
        for (int i = 1; i < size; i++) {
            double diff = prices.get(i) - prices.get(i - 1);
            sumOfList += diff;
        }

        return sumOfList;
    }
}

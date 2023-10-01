package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messages.order.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalLong;


public class MovingAverage implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MovingAverage.class);

    int SHORT_TERM_PERIOD = 3;
    int LONG_TERM_PERIOD = 7;

    List<Long> askHistoricalPrices = new ArrayList<>();
    List<Long> bidHistoricalPrices = new ArrayList<>();

    /***
     *
     * @param state represents the current market data and trading environment
     * @return an action e.g. createChildOrder or NoAction.NoAction etc.
     */
    @Override
    public Action evaluate(SimpleAlgoState state) {
        final String orderBookAsString = Util.orderBookToString(state);
        logger.info("[MOVINGAVERAGE] The state of the order book is:\n" + orderBookAsString);
        final BidLevel bidLevel = state.getBidAt(0);
        final AskLevel askLevel = state.getAskAt(0);
        long bidQuantity = 100;
        long askQuantity = 0;
        var totalOrderCount = state.getChildOrders().size();

        if (totalOrderCount > 19) {
            logger.info("[MOVINGAVERAGE]: we have " + totalOrderCount + " orders in the market. Want " + totalOrderCount
                    + " done.");
            return NoAction.NoAction;
        }

        if (askLevel != null && bidLevel != null) {
            long bidLevelPrice = bidLevel.price;
            long askLevelPrice = askLevel.price;

            List<Long> gatherBidHistoricalPrices = getHistoricalPrices(bidHistoricalPrices, bidLevelPrice);
            List<Long> gatherAskHistoricalPrices = getHistoricalPrices(askHistoricalPrices, askLevelPrice);

            long askShortTermMovingAverage = calculateSMA(gatherAskHistoricalPrices, SHORT_TERM_PERIOD);
            long askLongTermMovingAverage = calculateSMA(gatherAskHistoricalPrices, LONG_TERM_PERIOD);

            long bidShortTermMovingAverage = calculateSMA(gatherBidHistoricalPrices, SHORT_TERM_PERIOD);
            long bidLongTermMovingAverage = calculateSMA(gatherBidHistoricalPrices, LONG_TERM_PERIOD);


            OptionalLong filledQuantitySum = state.getChildOrders().stream().mapToLong(ChildOrder::getFilledQuantity)
                    .reduce(Long::sum);
            long filledQuantity = filledQuantitySum.orElse(0L);

            //        todo - question - how do i reset filled quantity back to zero once should sell get's fullfilled
            //         and set askQuantity back to zero once sell get's fulfilled?

            if (filledQuantity == 0) {

                if (shouldBuy(askShortTermMovingAverage, askLongTermMovingAverage)) {
                    logger.info("[MOVINGAVERAGE]: Adding buy order for: quantity = " + bidQuantity + " @ price=" + bidLevelPrice);
                    return new CreateChildOrder(Side.BUY, bidQuantity, bidLevelPrice);

                }
            } else if (filledQuantity > 0) {
                askQuantity = filledQuantity;
                if (shouldSell(bidShortTermMovingAverage, bidLongTermMovingAverage)) {
                    logger.info("[MOVINGAVERAGE]: Adding sell order for: quantity=" + askQuantity + " @ price="
                            + askLevelPrice);
                    Action ask = new CreateChildOrder(Side.SELL, askQuantity, askLevelPrice);
//             todo-       reset ask quantity and filledQuantity back to zero if once sell has been fullfilled
                    filledQuantity -= filledQuantitySum.orElse(0L);
                    askQuantity = 0L;
                    return ask;
                }
            }
        }

        logger.info("[MOVINGAVERAGE]: No orders to execute");
        return NoAction.NoAction;
    }

    /***
     *
     * @param historicalPrices  a list for storing historical best prices of bid or ask.
     * @param price best prices of bid or ask.
     * @return historical prices of bid or ask.
     */
    public List<Long> getHistoricalPrices(List<Long> historicalPrices, long price) {
        historicalPrices.add(price);
        return historicalPrices;
    }

    /***
     *
     * @param historicalPrices a list that stores the historical best prices of bid or ask.
     * @param termPeriod short term of long term period of bid or ask.
     * @return the average of the best ask and bid prices.
     */
    public long calculateSMA(List<Long> historicalPrices, int termPeriod) {
        OptionalDouble average;
        if (historicalPrices.size() < termPeriod) {
            average = historicalPrices.stream().mapToLong(Long::longValue).average();
        } else {
            var sliceHistPrices = historicalPrices.subList(historicalPrices.size() - termPeriod, historicalPrices.size());
            average = sliceHistPrices.stream().mapToLong(Long::longValue).average();
        }
        return (long) average.orElse(0L);
    }

    /**
     * @param askLatestShortTermSMA the average of the short term SMA
     * @param askLatestLongTermSMA  the average of the long term SMA
     * @return true and initiates a buy order in the evaluate method when
     * the short-term SMA crosses above the long-term SMA (Golden Cross) otherwise false
     */
    public boolean shouldBuy(long askLatestShortTermSMA, long askLatestLongTermSMA) {
        return askLatestShortTermSMA > askLatestLongTermSMA;
    }

    /**
     * @param bidLatestShortTermSMA the average of the short term SMA
     * @param bidLatestLongTermSMA  the average of the long term SMA
     * @return true and initiates a sell order in the evaluate method when
     * the short-term SMA crosses below the long-term SMA (Death Cross) otherwise false
     */
    public boolean shouldSell(long bidLatestShortTermSMA, long bidLatestLongTermSMA) {
        return bidLatestShortTermSMA < bidLatestLongTermSMA;
    }
}

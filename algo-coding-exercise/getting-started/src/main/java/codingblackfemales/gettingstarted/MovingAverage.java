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
import messages.order.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovingAverage implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MovingAverage.class);

    int SHORT_TERM_PERIOD = 3;
    int LONG_TERM_PERIOD = 7;

    List<Long> askHistoricalPrices = new ArrayList<>();
    List<Long> bidHistoricalPrices = new ArrayList<>();


    @Override
    public Action evaluate(SimpleAlgoState state) {

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[MOVINGAVERAGE] The state of the order book is:\n" + orderBookAsString);
        final BidLevel bid = state.getBidAt(0);
        final AskLevel ask = state.getAskAt(0);
        long bidPrice = bid.price;
        long askPrice = ask.price;
        long bidQuantity = 100;
        long askQuantity = 0;

        var totalOrderCount = state.getChildOrders().size();

        if (totalOrderCount > 19) {
            logger.info("[MOVINGAVERAGE]: we have " + totalOrderCount + " orders in the market. Want " + totalOrderCount
                    + " done.");
            return NoAction.NoAction;
        }

        List<Long> gatherBidHistoricalPrices = getHistoricalPrices(bidHistoricalPrices, bidPrice);
        logger.info("bidHistoricalPrices: " + bidHistoricalPrices);

        List<Long> gatherAskHistoricalPrices = getHistoricalPrices(askHistoricalPrices, askPrice);
        logger.info("askHistoricalPrices: " + askHistoricalPrices);

        long askShortTermMovingAverage = calculateSMA(gatherAskHistoricalPrices, SHORT_TERM_PERIOD);
        long askLongTermMovingAverage = calculateSMA(gatherAskHistoricalPrices, LONG_TERM_PERIOD);

        long bidShortTermMovingAverage = calculateSMA(gatherBidHistoricalPrices, SHORT_TERM_PERIOD);
        long bidLongTermMovingAverage = calculateSMA(gatherBidHistoricalPrices, LONG_TERM_PERIOD);


        OptionalLong filledQuantitySum = state.getChildOrders().stream().mapToLong(ChildOrder::getFilledQuantity)
                .reduce(Long::sum);
        long filledQuantity = filledQuantitySum.orElse(0L);

        if (filledQuantity == 0) {
            if (shouldBuy(askShortTermMovingAverage, askLongTermMovingAverage)) {
                logger.info("[MOVINGAVERAGE]: Adding buy order for: quantity = " + bidQuantity + " @ price=" + bidPrice);
                return new CreateChildOrder(Side.BUY, bidQuantity, bidPrice);
            }
        } else if (filledQuantity > 0) {
            if (shouldSell(bidShortTermMovingAverage, bidLongTermMovingAverage)) {
                askQuantity = filledQuantity;
                logger.info("[MOVINGAVERAGE]: Adding sell order for: quantity=" + askQuantity + " @ price="
                        + askPrice);
                return new CreateChildOrder(Side.SELL, askQuantity, askPrice);
            }
        } else if (filledQuantity < askQuantity) {
            filledQuantity = 0;
        }

        logger.info("[MOVINGAVERAGE]: No orders to execute");
        return NoAction.NoAction;
    }

    // get historical prices at level 0
    public List<Long> getHistoricalPrices(List<Long> historicalPrices, long price) {
        historicalPrices.add(price);
        return historicalPrices;
    }

    public long calculateSMA(List<Long> historicalPrices, int termPeriod) {
        OptionalDouble average;
        if (historicalPrices.size() < termPeriod) {
            average = historicalPrices.stream().mapToLong(Long::longValue).average();

        } else {
            var sliceHistPrices = historicalPrices.subList(termPeriod, historicalPrices.size());
            average = sliceHistPrices.stream().mapToLong(Long::longValue).average();
        }
        return (long) average.orElse(0L);
    }


    // make money when price is going down - should buy
    public boolean shouldBuy(long askLatestShortTermSMA, long askLatestLongTermSMA) {
        // Check if the short-term SMA crosses above the long-term SMA (Golden Cross)
        return askLatestShortTermSMA > askLatestLongTermSMA;
    }

    // make money when price is going up - should sell
    public boolean shouldSell(long bidLatestShortTermSMA, long bidLatestLongTermSMA) {
        // Check if the long-term SMA is greater than the short-term SMA (Death Cross)
        return bidLatestShortTermSMA < bidLatestLongTermSMA;
    }
}
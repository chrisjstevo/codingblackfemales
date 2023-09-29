package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
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
    // Todo - problem. buys multiple stocks at the best price in tick 1
    // it's first selling before it buys
    // it should buy first and only after then should it sell
    private static final Logger logger = LoggerFactory.getLogger(MovingAverage.class);

    int SHORT_TERM_PERIOD = 2;
    int LONG_TERM_PERIOD = 4;

    List<Long> askHistoricalPrices = new ArrayList<>();
    List<Long> bidHistoricalPrices = new ArrayList<>();

    // For storing the quantity of all the stocks I bought
    // todo my stock quantity should be a number start from 0 then increase as
    // quantity increases
    long myStockQuantity = 0L;

    @Override
    public Action evaluate(SimpleAlgoState state) {

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[MOVINGAVERAGE] The state of the order book is:\n" + orderBookAsString);
        final BidLevel bid = state.getBidAt(0);
        final AskLevel ask = state.getAskAt(0);
        long bidPrice = bid.price;
        long askPrice = ask.price;
        long buyQuantity = 100;

        var totalOrderCount = state.getChildOrders().size();

        if (totalOrderCount > 9) {
            logger.info("[MOVINGAVERAGE]: we have " + totalOrderCount + " orders in the market. Want " + totalOrderCount
                    + " done.");
            return NoAction.NoAction;
        }

        var gatherBidHistoricalPrices = getHistoricalPrices(bidHistoricalPrices, bidPrice);
        logger.info("bidHistoricalPrices: " + bidHistoricalPrices);

        var gatherAskHistoricalPrices = getHistoricalPrices(askHistoricalPrices, askPrice);
        logger.info("askHistoricalPrices: " + askHistoricalPrices);

        var askLongTermMovingAverage = calculateSMA(gatherAskHistoricalPrices, LONG_TERM_PERIOD);
        var askShortTermMovingAverage = calculateSMA(gatherAskHistoricalPrices, SHORT_TERM_PERIOD);

        var bidLongTermMovingAverage = calculateSMA(gatherBidHistoricalPrices, LONG_TERM_PERIOD);
        var bidShortTermMovingAverage = calculateSMA(gatherBidHistoricalPrices, SHORT_TERM_PERIOD);


        OptionalLong filledQuantityOptional = state.getChildOrders().stream().mapToLong(ChildOrder::getFilledQuantity)
                .reduce(Long::sum);
        long filledQuantity = filledQuantityOptional.orElse(0L);

        if (filledQuantity == 0) {

            if (shouldBuy(askLongTermMovingAverage, askShortTermMovingAverage)) {

                logger.info(
                        "[MOVINGAVERAGE]: Adding buy order for: quantity = " + buyQuantity + " @ price=" + bidPrice);
                addAvailableQuantity(buyQuantity);
                return new CreateChildOrder(Side.BUY, buyQuantity, bidPrice);

            }

        } else if (filledQuantity > 0 && getAvailableQuantity() > 0) {
            if (shouldSell(bidLongTermMovingAverage, bidShortTermMovingAverage)) {
                // do not sell at a price less than or equals to the price you bought the stock
//                if (askPrice > bidPrice) {
                var availableQuantity = getAvailableQuantity();
                logger.info("availableQuantity: " + availableQuantity);
                subtractAvailableQuantity(availableQuantity);
                // todo - sell all the quantity you bought.
                logger.info("[MOVINGAVERAGE]: Adding sell order for: quantity=" + availableQuantity + " @ price="
                        + askPrice);
                return new CreateChildOrder(Side.SELL, availableQuantity, askPrice);
//                }
            }
        } else {
            if (shouldBuy(askLongTermMovingAverage, askShortTermMovingAverage)) {

                logger.info(
                        "[MOVINGAVERAGE]: Adding buy order for: quantity = " + buyQuantity + " @ price=" + bidPrice);
                addAvailableQuantity(buyQuantity);
                return new CreateChildOrder(Side.BUY, buyQuantity, bidPrice);

            }
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


    public long addAvailableQuantity(long quantity) {
        return myStockQuantity = myStockQuantity + quantity;
    }

    public long getAvailableQuantity() {
        return myStockQuantity;
    }

    public long subtractAvailableQuantity(long quantity) {
        return myStockQuantity = myStockQuantity - quantity;
    }

    public long getFilledQuantityMA(SimpleAlgoState state) {
        OptionalLong filledQuantityOptional = state.getChildOrders().stream().mapToLong(ChildOrder::getFilledQuantity)
                .reduce(Long::sum);
        return filledQuantityOptional.orElse(0L);
//    logger.info("filledQuantity: " + filledQuantity);
//    var activeOrderChild = state.getActiveChildOrders().size();
    }
    // make money when price is going down - should buy
    // make money when price is going up - should sell

    public boolean shouldBuy(long askLatestLongTermSMA, long askLatestShortTermSMA) {
        // Check if the short-term SMA crosses above the long-term SMA (Golden Cross)
        return askLatestShortTermSMA > askLatestLongTermSMA;
    }

    public boolean shouldSell(long bidLatestLongTermSMA, long bidLatestShortTermSMA) {
        // Check if the long-term SMA is greater than the short-term SMA (Death Cross)
        return bidLatestShortTermSMA < bidLatestLongTermSMA;
    }
}
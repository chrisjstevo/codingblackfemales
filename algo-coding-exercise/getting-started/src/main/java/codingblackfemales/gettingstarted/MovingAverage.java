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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovingAverage implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MovingAverage.class);

    int SHORT_TERM_PERIOD = 2;
    int LONG_TERM_PERIOD = 5;

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

        long quantity = 100;

        var totalOrderCount = state.getChildOrders().size();

        if (totalOrderCount > 50) {
            logger.info("[MOVINGAVERAGE]: we have " + totalOrderCount + " orders in the market. Want " + totalOrderCount
                    + " done.");
            return NoAction.NoAction;
        }

        var askLongTermMovingAverage = calculateSMA(getAskHistoricalPrices(askPrice), LONG_TERM_PERIOD);
        var askShortTermMovingAverage = calculateSMA(getAskHistoricalPrices(askPrice), SHORT_TERM_PERIOD);
        var bidLongTermMovingAverage = calculateSMA(getBidHistoricalPrices(bidPrice), LONG_TERM_PERIOD);
        var bidShortTermMovingAverage = calculateSMA(getBidHistoricalPrices(bidPrice), SHORT_TERM_PERIOD);

        var filledPrice = state.getChildOrders().stream().mapToLong(ChildOrder::getPrice).sum();

        if (shouldBuy(askLongTermMovingAverage, askShortTermMovingAverage)) {

            logger.info("[MOVINGAVERAGE]: Adding buy order for: quantity = " + quantity + " @ price=" + bidPrice);
            return new CreateChildOrder(Side.BUY, quantity, bidPrice);

            // Todo - do not sell at a price less than or equals to the price you bought the stock
        } else if (shouldSell(bidLongTermMovingAverage, bidShortTermMovingAverage)) {

            if (askPrice > filledPrice) {
                logger.info("[MOVINGAVERAGE]: Adding sell order for: quantity=" + quantity + " @ price=" + askPrice);
                return new CreateChildOrder(Side.SELL, ask.quantity, askPrice);
            }
        }

        logger.info("[MOVINGAVERAGE]: No orders to execute");
        return NoAction.NoAction;
    }


    public List<Long> getAskHistoricalPrices(long price) {
        askHistoricalPrices.add(price);
        logger.info("askHistoricalPrices: " + askHistoricalPrices);
        return askHistoricalPrices;
    }

    public List<Long> getBidHistoricalPrices(long price) {
        bidHistoricalPrices.add(price);
        logger.info("bidHistoricalPrices: " + bidHistoricalPrices);
        return bidHistoricalPrices;
    }


    public double calculateSMA(List<Long> historicalPrices, int termPeriod) {
        if (historicalPrices.size() < termPeriod) {
            return historicalPrices.stream().mapToDouble(Long::longValue).sum() / Math.min(termPeriod, historicalPrices.size());
        } else {
            var sliceHistPrices = historicalPrices.subList(termPeriod, historicalPrices.size());
            return sliceHistPrices.stream().mapToDouble(Long::longValue).sum()
                    / Math.min(termPeriod, historicalPrices.size());
        }
    }


    public boolean shouldBuy(double askLatestLongTermSMA, double askLatestShortTermSMA) {

        // Check if the short-term SMA crosses above the long-term SMA (Golden Cross)
        return askLatestShortTermSMA > askLatestLongTermSMA;
    }

    public boolean shouldSell(double bidLatestLongTermSMA, double bidLatestShortTermSMA) {
        // Check if the long-term SMA is greater than the short-term SMA (Death Cross)
        return bidLatestShortTermSMA < bidLatestLongTermSMA;
    }
}
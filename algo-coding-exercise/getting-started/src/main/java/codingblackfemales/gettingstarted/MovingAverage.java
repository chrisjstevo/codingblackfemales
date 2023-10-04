package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;

import codingblackfemales.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messages.order.Side;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

//Todo - clean up
public class MovingAverage implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MovingAverage.class);

    int SHORT_TERM_PERIOD = 3;
    int LONG_TERM_PERIOD = 7;

    List<Long> askHistoricalPrices = new ArrayList<>();

    long buyAtPrice = 0;

    long counter = 0;

    /***
     *
     * @param state represents the current market data and the trading environment
     * @return an action e.g. createChildOrder, cancelChildOrder or
     *         NoAction.NoAction etc.
     */
    @Override
    public Action evaluate(SimpleAlgoState state) {
        final String orderBookAsString = Util.orderBookToString(state);
        logger.info("[MOVINGAVERAGE] The state of the order book is:\n" + orderBookAsString);

        final AskLevel askLevel = state.getAskAt(0);
        long bidQuantity = 100;

        var totalOrderCount = state.getChildOrders().size();

        if (totalOrderCount > 29) {
            logger.info("[MOVINGAVERAGE]: we have " + totalOrderCount + " orders in the market. Want " + totalOrderCount
                    + " done.");
            return NoAction.NoAction;
        }

        if (askLevel != null) {
            long askLevelPrice = askLevel.price;

            askHistoricalPrices = getHistoricalPrices(askHistoricalPrices, askLevelPrice);

            long askShortTermMovingAverage = calculateSMA(askHistoricalPrices, SHORT_TERM_PERIOD);
            long askLongTermMovingAverage = calculateSMA(askHistoricalPrices, LONG_TERM_PERIOD);

            // I am using supplier because stream can be consumed only once. I am using
            // Supplier in order to use
            // stream multiple times more info:
            // https://stackoverflow.com/questions/52689103/java-8-once-stream-is-consumed-and-operated-giving-error-but-in-another-case
            Supplier<Stream<ChildOrder>> activeChildOrdersSupplier = () -> state.getActiveChildOrders().stream();

            Stream<ChildOrder> myChildOrdersBuy = activeChildOrdersSupplier.get()
                    .filter(order -> order.getSide() == Side.BUY);
            Stream<ChildOrder> myChildOrdersSell = activeChildOrdersSupplier.get()
                    .filter(order -> order.getSide() == Side.SELL);

            long filledBuyOrderQty = myChildOrdersBuy.mapToLong(ChildOrder::getFilledQuantity).reduce(Long::sum)
                    .orElse(0L);
            long sellOrderQty = myChildOrdersSell.mapToLong(ChildOrder::getQuantity).reduce(Long::sum).orElse(0L);

            long stockQty = filledBuyOrderQty - sellOrderQty;

            Supplier<Stream<ChildOrder>> pendingBuyOrders = () -> state.getActiveChildOrders().stream()
                    .filter(order -> order.getState() == OrderState.PENDING)
                    .filter(order -> order.getSide() == Side.BUY);

            Stream<ChildOrder> pendingSellOrders = activeChildOrdersSupplier.get()
                    .filter(order -> order.getState() == OrderState.PENDING)
                    .filter(order -> order.getSide() == Side.SELL);

            if (pendingSellOrders.toList().isEmpty() && pendingBuyOrders.get().toList().isEmpty()) {
                if ((shouldBuy(askShortTermMovingAverage, askLongTermMovingAverage)) && stockQty == 0) {
                    logger.info("[MOVINGAVERAGE]: Adding buy order for quantity: " + bidQuantity + " @ price="
                            + askLevelPrice);
                    buyAtPrice = askLevelPrice;
                    counter = 0;
                    return new CreateChildOrder(Side.BUY, bidQuantity, askLevelPrice);

                }
            } else if (counter >= 5) {
                final var option = pendingBuyOrders.get().findFirst();
                if (option.isPresent()) {
                    var childOrder = option.get();
                    logger.info("[MOVINGAVERAGE] Cancelling order: " + childOrder);

                    return new CancelChildOrder(childOrder);
                }
            } else if (stockQty > 0) {
                logger.info("[MOVINGAVERAGE]: Adding sell order for quantity: " + stockQty + " @ price="
                        + (long) (buyAtPrice * 1.01));

                return new CreateChildOrder(Side.SELL, stockQty, (long) (buyAtPrice * 1.01));

            } else if (!pendingBuyOrders.get().toList().isEmpty()) {
                counter += 1;
            }

        }

        logger.info("[MOVINGAVERAGE]: No orders to execute");
        return NoAction.NoAction;
    }

    /***
     *
     * @param historicalPrices a list for storing historical best prices of ask.
     * @param price            best prices of bid or ask.
     * @return historical prices of bid or ask.
     */
    public List<Long> getHistoricalPrices(List<Long> historicalPrices, long price) {
        historicalPrices.add(price);
        return historicalPrices;
    }

    /***
     *
     * @param historicalPrices a list that stores the historical best prices of ask
     * @param termPeriod       short term of long term period of bid or ask.
     * @return the average of the best ask and bid prices.
     */
    public long calculateSMA(List<Long> historicalPrices, int termPeriod) {
        OptionalDouble average;
        if (historicalPrices.size() < termPeriod) {
            average = historicalPrices.stream().mapToLong(Long::longValue).average();
        } else {
            var sliceHistPrices = historicalPrices.subList(historicalPrices.size() - termPeriod,
                    historicalPrices.size());
            average = sliceHistPrices.stream().mapToLong(Long::longValue).average();
        }
        return (long) average.orElse(0L);
    }

    /**
     * @param askLatestShortTermSMA the average of the short term SMA
     * @param askLatestLongTermSMA  the average of the long term SMA
     * @return true and initiates a buy order in the evaluate method when
     *         the short-term SMA crosses above the long-term SMA (Golden Cross)
     *         otherwise false
     */
    public boolean shouldBuy(long askLatestShortTermSMA, long askLatestLongTermSMA) {
        return askLatestShortTermSMA > askLatestLongTermSMA;
    }

}

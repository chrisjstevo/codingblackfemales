package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Stretch goal: buy when the market is cheap, sell when it is expensive.
 *
 * "Cheap" and "expensive" are judged against the average mid price
 * over the last WINDOW evaluations.
 */
public class MyStretchAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyStretchAlgoLogic.class);

    private static final int WINDOW = 5;              // how many recent prices to average
    private static final long QUANTITY = 75;          // size of each child order
    private static final int MAX_ACTIVE_ORDERS = 3;   // live orders allowed at once
    private static final int MAX_TOTAL_ORDERS = 20;   // safety cap so we never spam orders
    private static final long MAX_POSITION = 225;     // most we'll hold (bought + pending buys)

    // Remembered between calls to evaluate()
    private final Deque<Long> recentPrices = new ArrayDeque<>();

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[STRETCH] bidLevels=" + state.getBidLevels() + " askLevels=" + state.getAskLevels());

        // Need both sides of the book to judge the market
        if (state.getBidLevels() == 0 || state.getAskLevels() == 0) {
            return NoAction.NoAction;
        }

        var bidLevel = state.getBidAt(0);
        var askLevel = state.getAskAt(0);
        if (bidLevel == null || askLevel == null) {
            return NoAction.NoAction;
        }
        long bestBid = bidLevel.price;
        long bestAsk = askLevel.price;
        long mid = (bestBid + bestAsk) / 2;

        // Track the recent mid prices
        recentPrices.addLast(mid);
        if (recentPrices.size() > WINDOW) {
            recentPrices.removeFirst();
        }
        if (recentPrices.size() < WINDOW) {
            logger.info("[STRETCH] Warming up, " + recentPrices.size() + "/" + WINDOW + " prices seen");
            return NoAction.NoAction;
        }

        double average = recentPrices.stream().mapToLong(Long::longValue).average().orElse(mid);
        logger.info("[STRETCH] bid=" + bestBid + " ask=" + bestAsk + " avg=" + average);

        var activeOrders = state.getActiveChildOrders();

        // 1. Cancel stale buys: unfilled buys the market has moved away from
        for (ChildOrder order : activeOrders) {
            if (order.getSide() == Side.BUY && order.getFilledQuantity() == 0 && order.getPrice() < bestBid) {
                logger.info("[STRETCH] Cancelling stale buy " + order);
                return new CancelChildOrder(order);
            }
        }

        // Safety cap
        if (state.getChildOrders().size() >= MAX_TOTAL_ORDERS) {
            return NoAction.NoAction;
        }

        // 2. Work out what we hold
        long filledBuys = 0;
        long sellsPlaced = 0;
        for (ChildOrder order : state.getChildOrders()) {
            if (order.getSide() == Side.BUY) {
                filledBuys += order.getFilledQuantity();
            } else {
                sellsPlaced += order.getQuantity();
            }
        }
        long pendingBuys = 0;
        for (ChildOrder order : activeOrders) {
            if (order.getSide() == Side.BUY) {
                pendingBuys += order.getQuantity() - order.getFilledQuantity();
            }
        }
        long holding = filledBuys - sellsPlaced;

        // 3. Cheap: buy at the ask so we actually get filled
        if (bestAsk < average
                && activeOrders.size() < MAX_ACTIVE_ORDERS
                && holding + pendingBuys + QUANTITY <= MAX_POSITION) {
            logger.info("[STRETCH] Cheap, BUY " + QUANTITY + " @ " + bestAsk);
            return new CreateChildOrder(Side.BUY, QUANTITY, bestAsk);
        }

        // 4. Expensive: sell what we hold at the bid
        if (bestBid > average && holding >= QUANTITY) {
            logger.info("[STRETCH] Expensive, SELL " + QUANTITY + " @ " + bestBid);
            return new CreateChildOrder(Side.SELL, QUANTITY, bestBid);
        }

        return NoAction.NoAction;
    }
}
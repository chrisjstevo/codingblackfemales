package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger =
            LoggerFactory.getLogger(MyAlgoLogic.class);

    private static final long ORDER_QUANTITY = 10L;

    private boolean buySubmitted = false;
    private boolean cancelSubmitted = false;

    @Override
    public Action evaluate(SimpleAlgoState state) {

        // Wait until both sides of the market are available.
        if (state.getBidLevels() == 0 || state.getAskLevels() == 0) {
            return NoAction.NoAction;
        }

        var bestBid = state.getBidAt(0);
        var bestAsk = state.getAskAt(0);

        if (bestBid == null || bestAsk == null) {
            return NoAction.NoAction;
        }

        // Only act on valid, non-crossed market data.
        if (bestBid.getPrice() <= 0
                || bestAsk.getPrice() <= 0
                || bestBid.getQuantity() <= 0
                || bestAsk.getQuantity() <= 0
                || bestBid.getPrice() >= bestAsk.getPrice()) {
            return NoAction.NoAction;
        }

        // Submit one buy order during this algorithm's lifetime.
        if (!buySubmitted && state.getChildOrders().isEmpty()) {
            buySubmitted = true;

            logger.info("[MYALGO] Creating BUY: {} units at {}",
                    ORDER_QUANTITY, bestBid.getPrice());

            return new CreateChildOrder(
                    Side.BUY,
                    ORDER_QUANTITY,
                    bestBid.getPrice()
            );
        }

        // Cancel our unfilled buy if the best bid changes.
        if (!cancelSubmitted) {
            for (var order : state.getActiveChildOrders()) {
                if (order.getSide() == Side.BUY
                        && order.getFilledQuantity() < order.getQuantity()
                        && order.getPrice() != bestBid.getPrice()) {

                    cancelSubmitted = true;

                    logger.info(
                            "[MYALGO] Cancelling order {}: best bid changed from {} to {}",
                            order.getOrderId(),
                            order.getPrice(),
                            bestBid.getPrice()
                    );

                    return new CancelChildOrder(order);
                }
            }
        }

        return NoAction.NoAction;
    }
}
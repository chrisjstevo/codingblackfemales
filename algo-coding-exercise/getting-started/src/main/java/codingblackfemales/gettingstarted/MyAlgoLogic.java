package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    private static final long BUY_QUANTITY = 55;
    private static final int MAX_TOTAL_ORDERS = 4;

    private long countFilled(SimpleAlgoState state, Side side) {
        return state.getChildOrders().stream()
                .filter(order -> order.getSide() == side)
                .filter(order -> order.getFilledQuantity() > 0)
                .count();
    }


    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel bestBid = state.getBidAt(0);
        final int allChildOrder = state.getChildOrders().size();

      //Safety cap: stops algo from creating too many orders
        if (allChildOrder > MAX_TOTAL_ORDERS) {
            return NoAction.NoAction;
        }

       // Cancel only unfilled buys priced below the best bid
        // Filled orders stay in the active list so exclude them
        final var staleBuy = state.getActiveChildOrders().stream()
                .filter(order -> order.getSide() == Side.BUY)
                .filter(order -> order.getFilledQuantity() == 0)
                .filter(order -> order.getPrice() < bestBid.price)
                .findFirst();

        if (staleBuy.isPresent()) {
            var activeOrder = staleBuy.get();
            logger.info("[MYALGO] Cancelling order:" + activeOrder);
            return new CancelChildOrder(activeOrder);
        }

        // Checks if orders have more filledBuy than filledSell means we hold something
        final boolean holdingPosition = countFilled(state, Side.BUY) - countFilled(state, Side.SELL) > 0;

        if (holdingPosition) {
            // first filled buy (doesn't check whether it was already sold)
            var filledBuy = state.getChildOrders().stream().filter(order ->
                    order.getSide() == Side.BUY && order.getFilledQuantity() > 0).findFirst();
            if (filledBuy.isPresent()) {
                final var childOrder = filledBuy.get();
                // Sell only at a profit: the best bid must be above what we paid
                if (childOrder.getPrice() < bestBid.price) {
                    logger.info("[MYALGO] Bought at " + childOrder.getPrice() + ", sell now at " + bestBid.price );
                    return new CreateChildOrder(Side.SELL, childOrder.getQuantity(), bestBid.price);
                } else {
                    return NoAction.NoAction;
                }
            } else {
                return NoAction.NoAction;
            }

        } else {
            // Not holding anything: buy passively at the best bid
            final long price = bestBid.price;
            final long quantity = BUY_QUANTITY;
            final boolean hasRestingBuy = state.getActiveChildOrders().stream()
                    .filter(order -> order.getSide() == Side.BUY)
                    .filter(order -> order.getFilledQuantity() == 0)
                    .findFirst()
                    .isPresent();
            if (!hasRestingBuy) {
                logger.info("[MYALGO] Not holding anything, buying " + quantity + " @ " + price);
                return new CreateChildOrder(Side.BUY, quantity, price);
            } else {
                return  NoAction.NoAction;
            }



        }






    }
}

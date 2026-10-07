package codingblackfemales.gettingstarted;
import codingblackfemales.sotw.SimpleAlgoState;
import static codingblackfemales.action.NoAction.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import codingblackfemales.util.Util;
import messages.order.Side;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel bidNearTouch = state.getBidAt(0);
        final AskLevel askNearTouch = state.getAskAt(0);

        final long quantity = 75;
        final long targetBuyPrice = 100;
        final long targetSellPrice = 105;
        final int maxActiveChildOrders = 3;

        final boolean isCheapEnoughToBuy = bidNearTouch.price < targetBuyPrice;
        final boolean isExpensiveEnoughToSell = askNearTouch.price > targetSellPrice;

        for (ChildOrder childOrder : state.getActiveChildOrders()) {
            final boolean isUnfilled = childOrder.getFilledQuantity() == 0;
            final boolean isBuyNoLongerCheap = childOrder.getSide() == Side.BUY && !isCheapEnoughToBuy;
            final boolean isSellNoLongerExpensive = childOrder.getSide() == Side.SELL && !isExpensiveEnoughToSell;

            if (isUnfilled && childOrder.getState() != OrderState.CANCELLED && (isBuyNoLongerCheap || isSellNoLongerExpensive)) {
                logger.info("[MYALGO] Cancelling child order because market data no longer meets target: " + childOrder);
                return new CancelChildOrder(childOrder);
            }
        }

        // create BUY child order if < 3 child orders present
        if(isCheapEnoughToBuy && state.getActiveChildOrders().size() < maxActiveChildOrders) {
            logger.info("[MYALGO] Creating BUY child order with quantity: " + quantity + " @ price: " + bidNearTouch.price);
            return new CreateChildOrder(Side.BUY, quantity, bidNearTouch.price);
        } 

        // create SELL child order if < 3 child orders present
        if(isExpensiveEnoughToSell && state.getActiveChildOrders().size() < maxActiveChildOrders) {
            logger.info("[MYALGO] Creating SELL child order with quantity: " + quantity  + " @ price: "  + askNearTouch.price);
            return new CreateChildOrder(Side.SELL, quantity, askNearTouch.price);
        } 

        logger.info("[MYALGO] No action taken.");
        return NoAction;
    }
}

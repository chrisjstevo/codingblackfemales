package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import codingblackfemales.sotw.SimpleAlgoState;
import messages.order.Side;

import static codingblackfemales.action.NoAction.NoAction;

public class MyAlgoLogic implements AlgoLogic {

    // The quantity we want to buy
    private static final long TARGET_QUANTITY = 75;

    @Override
    public Action evaluate(SimpleAlgoState state) {
        long filledQuantity = 0;
        ChildOrder activeOrder = null;

        // Count fills even from cancelled orders, and find the outstanding order
        for (ChildOrder order : state.getChildOrders()) {
            filledQuantity += order.getFilledQuantity();
            if (order.getState() != OrderState.CANCELLED
                    && order.getFilledQuantity() < order.getQuantity()) {
                activeOrder = order;
            }
        }

        // Wait for valid prices and quantities on both sides of the book
        boolean validBook = state.getBidLevels() > 0 && state.getAskLevels() > 0
                && state.getBidAt(0) != null && state.getAskAt(0) != null
                && state.getBidAt(0).price > 0 && state.getAskAt(0).price > 0
                && state.getBidAt(0).quantity > 0 && state.getAskAt(0).quantity > 0
                && state.getBidAt(0).price < state.getAskAt(0).price;

        if (activeOrder != null) {
            // Cancel an outdated order. On the next evaluation, replace it
            if (!validBook || activeOrder.getPrice() != state.getBidAt(0).price) {
                return new CancelChildOrder(activeOrder);
            }
            return NoAction;
        }

        if (validBook && filledQuantity < TARGET_QUANTITY) {
            long price = state.getBidAt(0).price;
            long quantity = TARGET_QUANTITY - filledQuantity;
            return new CreateChildOrder(Side.BUY, quantity, price);
        }

        return NoAction;
    }
}

package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        //get order-book state
        var orderBookAsString = Util.orderBookToString(state);
        logger.info("**[MYALGO]** The state of the order book is:\n" + orderBookAsString);

        //get the current child orders
        var childOrders = state.getChildOrders();
        var activeOrders = state.getActiveChildOrders();

        //create child orders and cancel the oldest child order that is not filled
        if (activeOrders.size() < 4) {
            long[] newChildOrder = getNewMidChildOrder(state);

            logger.info("**[MYALGO]** Creating new child bid order with Quantity: " + newChildOrder[0] + " at Price: " + newChildOrder[1]);
            return new CreateChildOrder(Side.BUY, newChildOrder[0], newChildOrder[1]);
        } else {
            ChildOrder oldestCreatedOrder = childOrders.get(0);
            ChildOrder oldestActiveOrder = activeOrders.get(0);

            if ((oldestCreatedOrder.getFilledQuantity() == 0)
                    && (oldestCreatedOrder.getOrderId() == oldestActiveOrder.getOrderId())) {
                logger.info("**[MYALGO]** Cancelling unfilled child bid order with id: " + activeOrders.get(0).getOrderId());
                return new CancelChildOrder(state.getChildOrders().get(0));
            } else {
                return new NoAction();
            }
        }
    }

    public static long[] getNewMidChildOrder(SimpleAlgoState state) {
        long[] midChildOrder = new long[2];

        BidLevel topBid = state.getBidAt(0);
        AskLevel topAsk = state.getAskAt(0);

        long totalQuantity = topAsk.quantity + topBid.quantity;
        long midQuantity = totalQuantity / 2;
        long midPrice = (topAsk.price * topAsk.quantity + topBid.price * topBid.quantity) / totalQuantity;

        midChildOrder[0] = midQuantity;
        midChildOrder[1] = midPrice;

        return midChildOrder;
    }
}
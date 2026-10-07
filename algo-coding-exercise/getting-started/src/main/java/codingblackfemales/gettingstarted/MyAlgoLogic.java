package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messages.order.Side;
import codingblackfemales.action.CancelChildOrder;



public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        var activeOrders = state.getActiveChildOrders();
       
        
        if (activeOrders.isEmpty()) {
                BidLevel level = state.getBidAt(0);

                final long price = level.price;
                final long quantity = level.quantity;
                logger.info("[MYALGO] No active orders. Creating BUY order for " + quantity + " @ " + price);
                return new CreateChildOrder(Side.BUY, quantity, price);
        } else {
            var childOrder = activeOrders.get(0);
            BidLevel level = state.getBidAt(0);
            if (childOrder.getPrice() != level.price) {
                return new CancelChildOrder(childOrder);
            }

        }


        return NoAction.NoAction;
    }
}

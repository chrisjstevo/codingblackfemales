package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("THIS IS THE MY ALGO LOGIC");

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel levelOfBid = state.getBidAt(0);

        var TotalChildOrders = state.getChildOrders().size();

        
        
        long price = 90L;
        long quantity = levelOfBid.quantity;

        //create child orders

        if (TotalChildOrders < 2)
        {
            logger.info("[My ALGO] Have:" + state.getChildOrders().size() + " children, want 2, joining my algo with: " + quantity + " @ " + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        }
        
        //cancelling child order
        if (TotalChildOrders < 5) {
        
            final var activeOrders = state.getActiveChildOrders();
            final var child = activeOrders.stream().findFirst();

            if (activeOrders.size() > 0) {

                var ChildOrder = child.get();
                logger.info("[ADDCANCELALGO] Cancelling order:" + ChildOrder);
                return new CancelChildOrder(ChildOrder);
            }

                else {
                    return NoAction.NoAction;
                }
        }

        else {
            logger.info("[My ALGO] Have:" + state.getChildOrders().size() + " children, want 2, done.");
            return NoAction.NoAction;
        }
    

    }
}

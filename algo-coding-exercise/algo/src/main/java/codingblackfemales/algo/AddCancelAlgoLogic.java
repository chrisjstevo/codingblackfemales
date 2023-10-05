package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;

public class AddCancelAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(AddCancelAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[ADDCANCELALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[ADDCANCELALGO] Algo Sees Book as:\n" + book);

       // var totalOrderCount = state.getChildOrders().size();

        //make sure we have an exit condition...
        // if (totalOrderCount > 20) {
        //     return NoAction.NoAction;
        // }

        final var activeOrders = state.getActiveChildOrders();

        if (activeOrders.size() > 3) {

            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[ADDCANCELALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            }
            else{
                return NoAction.NoAction;
            }
        } else {
            BidLevel level = state.getBidAt(0);
            final long price = level.price;
            final long quantity = 75;

            if(state.getChildOrders().size() < 3){
                logger.info("[ADDCANCELALGO] Adding order for" + quantity + "@" + price);
                return new CreateChildOrder(Side.BUY, quantity, price);
            } else{
               logger.info("[ADDCANCELALGO] Have:" + state.getChildOrders().size() + " children, want 3, done.");
               return NoAction;
        }
            
        }
    }
}

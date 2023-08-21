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

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[MYALGO] In Algo Logic....");

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel bidLevel = state.getBidAt(0);

        var totalChildOrders = state.getChildOrders().size();

        long quantity1 = 30;
        long price1 = bidLevel.price;

        final var activeOrders = state.getActiveChildOrders();

        //create new child orders if we have less than 5
        if(totalChildOrders < 5){

            logger.info("[MYALGO] Have:" + totalChildOrders + " children, want 5, done.");
            return new CreateChildOrder(Side.BUY, quantity1, price1);
        }
        // if current child orders are less than 7 then cancel all
        if (totalChildOrders < 7) {
            final var option = activeOrders.stream().findFirst();

            if (activeOrders.size() > 0) {

                //check if we have pending orders
                if (option.isPresent()) {
                    var childOrder = option.get();
                    logger.info("[MYALGO] Cancelling order:" + childOrder);
                    return new CancelChildOrder(childOrder);
                } else {
                    return NoAction.NoAction;
                }

            } else {
                logger.info("[MYALGO] Have:" + totalChildOrders + " children, want 5, done.");
                return NoAction.NoAction;
            }
        }
        else{
            return NoAction.NoAction;
        }

    }
}

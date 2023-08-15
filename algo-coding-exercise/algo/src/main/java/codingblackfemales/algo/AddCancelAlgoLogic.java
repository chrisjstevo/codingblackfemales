package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// add order passively in the book and then cancel that order
public class AddCancelAlgoLogic implements AlgoLogic {
// cancel an order and then add it pass
    private static final Logger logger = LoggerFactory.getLogger(AddCancelAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {
        // This is just logging
        logger.info("[ADDCANCELALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[ADDCANCELALGO] Algo Sees Book as:\n" + book);

        var totalOrderCount = state.getChildOrders().size();

        // if I've got order in the book cancel it until i have got 20 fully life orders
        // and then stop
        // make sure we have an exit condition...
        // if i have more than 20 child orders in the market then stop
        // if i have done 20 orders whether they are on the market or they just cease
        // then stop, that’s your exit condition
        if (totalOrderCount > 20) {
            return NoAction.NoAction;
        }

        final var activeOrders = state.getActiveChildOrders();

        // if I have got an active order in the order book than I'm gonna cancel it
        if (activeOrders.size() > 0) {

            final var option = activeOrders.stream().findFirst();
            // if child order is present cancel the
            // child order you just got and it will
            // appear in my child order as cancelled
            if (option.isPresent()) {
                var childOrder = option.get();

                logger.info("[ADDCANCELALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            } else {
                return NoAction.NoAction;
            }
        } else {
            // get the bid, i am buying at level 0
            // it will sit on the passive side
            // it won't match immediately to the market data in the book
            // i then use that bid price and create a child buy for that quantity and price
            BidLevel level = state.getBidAt(0);
            final long price = level.price;
            final long quantity = level.quantity;
            logger.info("[ADDCANCELALGO] Adding order for" + quantity + "@" + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        }
    }
}

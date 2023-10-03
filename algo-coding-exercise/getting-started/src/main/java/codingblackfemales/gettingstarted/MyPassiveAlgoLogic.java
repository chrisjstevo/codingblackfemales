package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.algo.PassiveAlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;

public class MyPassiveAlgoLogic  implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(MyPassiveAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[MYALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[MYALGO] Algo Sees Book as:\n" + book);

        var totalOrderCount = state.getChildOrders().size();

        // Make sure we have an exit condition...
        if (totalOrderCount > 20) {
            return NoAction.NoAction;
        }

        final var activeOrders = state.getActiveChildOrders();

        if (activeOrders.size() > 0) {
            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[MYALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            } else {
                return NoAction.NoAction;
            }
        } else {
            BidLevel bidLevel = state.getBidAt(0);
            AskLevel askLevel = state.getAskAt(0);

            final long buyPrice = bidLevel.price;
            final long sellPrice = askLevel.price;

            final long buyQuantity = bidLevel.quantity;
            final long sellQuantity = askLevel.quantity;

            // Check if askLevel price is less than or equal to bidLevel price
            if (sellPrice <= buyPrice) {
                logger.info("MYPassiveALGO Adding order for " + buyQuantity + "@" + buyPrice);
                return new CreateChildOrder(Side.BUY, buyQuantity, buyPrice);
            } else {
                logger.info("MYPassiveALGO Adding order for " + sellQuantity + "@" + sellPrice);

                return new CreateChildOrder(Side.SELL, sellQuantity, sellPrice);

                //return NoAction.NoAction; // Do nothing if askLevel price is higher than bidLevel price
            }
        }
    }
}

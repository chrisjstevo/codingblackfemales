package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        var totalOrderCount = state.getChildOrders().size(); 
        BidLevel topOfBid = state.getBidAt(0);
        AskLevel topOfAsk = state.getAskAt(0);
        
        //exit condition
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

            } else if((topOfBid>topOfAsk) && state.getChildOrders().size() < 3) {
                BidLevel level = state.getBidAt(0);
                final long price = level.price;
                final long quantity = level.quantity;
                logger.info("[MYALGO] Buying order for" + quantity + "@" + price);
                return new CreateChildOrder(Side.BUY, quantity, price);

            } else if(topOfBid<topOfAsk) {
                AskLevel level = state.getAskAt(0);
                final long price = level.price;
                final long quantity = level.quantity;
                logger.info("[MYALGO] Selling order for" + quantity + "@" + price);
                return new CreateChildOrder(Side.SELL, quantity, price);
            }else {
                    return NoAction.NoAction;
            }            
    }
}

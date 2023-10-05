package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);
    
    @Override
    public Action evaluate(SimpleAlgoState state) {

        String orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        
        /********
         *
         * Add your logic here....
         *
         */

        AskLevel farTouch = state.getAskAt(0);

        final long price = 100;
        long quantityToBuy = 2000;
        long filledQuantity = 0;
        var activeOrders = state.getActiveChildOrders();
        
        
        // do not place more than 5 orders
        if (state.getChildOrders().size() > 4) {
            filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
            quantityToBuy -= filledQuantity;
            logger.info("[MyALGO] Has:" + state.getChildOrders().size() + " children and "+ filledQuantity+ " filledQuantity, done");
            return NoAction.NoAction;
        }
        
        // get filledQuantity
        if (activeOrders.size() >= 1) {
            filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
            quantityToBuy -= filledQuantity;
        }
        
        // if price matches, and quantity !=0, place order
        if (price >= farTouch.price && quantityToBuy > 0) {
            logger.info("[MyALGO] Has:" + state.getChildOrders().size() + 
            " children and "+ filledQuantity+ " filledQuantity, wants " + quantityToBuy + " quantity");
            return new CreateChildOrder(Side.BUY, quantityToBuy, price);

        // if price matches, and quantity is 0, do not place order
        } else if (price >= farTouch.price && quantityToBuy <= 0) {
            logger.info("[MyALGO] Has:" + state.getChildOrders().size() + 
            " children and "+ filledQuantity+ " filledQuantity");
            return NoAction.NoAction;

        // if price doesn't match, and quantity != 0, place order
        // activeOrders.size = 0 so order is placed on the passive side only once
        } else if (price < farTouch.price && quantityToBuy > 0 && activeOrders.size() == 0) {
            logger.info("[MyALGO] Has:" + state.getChildOrders().size() + " children and "+ 
            filledQuantity+ " filledQuantity, joining book with: " + quantityToBuy + " @ " + price);
            return new CreateChildOrder(Side.BUY, quantityToBuy, price);

        // if price doesn't match, and quantity is 0, do not place order
        } else return NoAction.NoAction;
    }
}


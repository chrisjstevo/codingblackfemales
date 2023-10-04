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

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        
        /********
         *
         * Add your logic here....
         *
         */

        final AskLevel farTouch = state.getAskAt(0);

        final long price = 80;
        final long myMaxBid = price+15;
        long quantityToBuy = 2000;
        long filledQuantity = 0;
        var activeOrders = state.getActiveChildOrders();
        
        
        // do not place more than 5 orders
        if (state.getChildOrders().size() > 4 && quantityToBuy !=0) {
            filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
            quantityToBuy -= filledQuantity;
            logger.info("[MyALGO] Has:" + state.getChildOrders().size() + " children and "+ filledQuantity+ " filledQuantity, done");
            return NoAction.NoAction;
        }
       
        
        // if price matches
        if (price >= farTouch.price && quantityToBuy !=0) {
            //when there's no active order, create an order
            if (activeOrders.size() == 0) {
                logger.info("[MyALGO] Has:" + state.getChildOrders().size() + " children and "+ filledQuantity+ " filledQuantity, joining book with: " + quantityToBuy + " @ " + price);
                return new CreateChildOrder(Side.BUY, quantityToBuy, price);
            } 

            //if there's atleast one order and price matches
            else if (activeOrders.size() >= 1 && farTouch.price<=myMaxBid) {
                filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
                quantityToBuy -= filledQuantity;
                logger.info("[MyALGO] Has:" + state.getChildOrders().size() + " children and"+ filledQuantity+ "filledQuantity");
                    
                if (quantityToBuy !=0) {
                    logger.info("[MyALGO] else-if if Has:" + state.getChildOrders().size() + " children and"+ filledQuantity+ "filledQuantity, joining book with: " + quantityToBuy + " @ " + myMaxBid);
                    return new CreateChildOrder(Side.BUY, quantityToBuy, myMaxBid);
                } else {
                    logger.info("[MyALGO] else-if else Has:" + state.getChildOrders().size() + " children and"+ filledQuantity+ "filledQuantity, joining book with: " + quantityToBuy + " @ " + myMaxBid); 
                    return NoAction.NoAction;
                }

            } else {
                filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
                quantityToBuy -= filledQuantity;
                logger.info("[MYALGO] else Has:" + state.getChildOrders().size() + " children, and " + filledQuantity+ " filledQuantity.");
                return new CreateChildOrder(Side.BUY, quantityToBuy, price);
            }  }
        else {
            // if there's an active order, update quantity
            if (activeOrders.size() >= 1) {
                filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
                quantityToBuy -= filledQuantity;
                logger.info("[MYALGO] last else if block Has:" + state.getChildOrders().size() + " children, and" + filledQuantity+ "filledQuantity.");
            } else {
            //no active order, create order.
            logger.info("[MYALGO] last else else block Has:" + state.getChildOrders().size() + " children, and" + filledQuantity+ "filledQuantity.");
        }
            return new CreateChildOrder(Side.BUY, quantityToBuy, price);
        }   
    }
}


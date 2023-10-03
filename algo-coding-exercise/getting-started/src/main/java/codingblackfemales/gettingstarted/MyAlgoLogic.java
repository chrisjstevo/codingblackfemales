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

import java.util.HashMap;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);


        /********
         * Add your logic here....
         * Check the number of active orders. If active orders < 3, then create a child order. When there are 3 child orders, stop creating new child orders.
         * BUY Child orders will be created on the passive side of the book to get a price as low as possible
         * SELL Child orders using sniper logic, as long as the price is higher than the buy price for that stock?
         * Cancel BUY child order to make space for SELL child order?
         * Create hashmap to store the quantities and prices that they've been bought
         * Cancel orders if going into loses, sell order
         * Create method for cancel order and wait for it to be executed.
         */

        final BidLevel nearTouch = state.getBidAt(0);
        long bidQuantity = 50;
        long bidPrice = nearTouch.price;

        final AskLevel farTouch = state.getAskAt(0);
        long askQuantity = farTouch.quantity;
        long askPrice = farTouch.price;

        var totalOrderCount = state.getChildOrders().size();
        var activeOrderCount = state.getActiveChildOrders().size();


//        logger.info("Filled price is: " + filledPrice);
//        logger.info("Average filled price is: " + averageFilledPrice);


        // If total order count is less than 20 generate and delete fake orders.
        if (totalOrderCount < 20) {
            if (activeOrderCount > 0) {

                var option = state.getActiveChildOrders().stream().findFirst();

                if (option.isPresent()) {
                    var childOrder = option.get();
                    return new CancelChildOrder(childOrder);
                } else {
                    return NoAction.NoAction;
                }
            } else {
                return new CreateChildOrder(Side.BUY, bidQuantity, bidPrice);
            }
        }

        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        long filledPrice = state.getActiveChildOrders().stream().map(ChildOrder::getPrice).reduce(Long::sum).get();
        long averageFilledPrice = filledPrice/activeOrderCount;

        //create my 3 child orders on the buy side
        if(activeOrderCount < 3){
            //then keep creating a new one
            logger.info("[PASSIVEALGO] Have:" + activeOrderCount + " active children, want 3, joining passive side of book with: " + bidQuantity + " @ " + bidPrice);
            return new CreateChildOrder(Side.BUY, bidQuantity, bidPrice);
//        } else if (activeOrderCount == 3 && averageFilledPrice < askPrice) {
//            logger.info("[PASSIVEALGO] Have:" + activeOrderCount + " active children, creating sell order, joining passive side of book with: " + filledQuantity + " @ " + askPrice);
//            return new CreateChildOrder(Side.SELL, filledQuantity,askPrice);
        } else{
            logger.info("[PASSIVEALGO] Done.");
            return NoAction.NoAction;
        }




    }

}

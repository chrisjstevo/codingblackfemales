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
         */

        final BidLevel nearTouch = state.getBidAt(0);
        long bidQuantity = 100;
        long bidPrice = 115;

        var totalOrderCount = state.getChildOrders().size();
        var activeOrderCount = state.getActiveChildOrders().size();



        // If total order count is less than 10 generate and delete fake orders.
        if (totalOrderCount < 10) {
            if (activeOrderCount > 0) {

                var option = state.getActiveChildOrders().stream().findFirst();

                if (option.isPresent()) {
                    var childOrder = option.get();
                    return new CancelChildOrder(childOrder);
                } else {
                    return NoAction.NoAction;
                }
            } else {
                return new CreateChildOrder(Side.BUY, bidQuantity, nearTouch.price);
            }
        }

        //create my 3 child orders on the buy side
        if(activeOrderCount < 3){
            //then keep creating a new one
            logger.info("[PASSIVEALGO] Have:" + activeOrderCount + " active children, want 3, joining passive side of book with: " + bidQuantity + " @ " + bidPrice);
            return new CreateChildOrder(Side.BUY, bidQuantity, bidPrice);
        } else{
            logger.info("[PASSIVEALGO] Done.");
            return NoAction.NoAction;
        }




    }

}

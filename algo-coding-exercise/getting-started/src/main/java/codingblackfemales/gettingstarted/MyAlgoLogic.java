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

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        /********
         *
         * Add your logic here....
         *
         */

        logger.info("[MYALGO] Algo Sees SheZa");
        final String book = Util.orderBookToString(state);

        logger.info("[MYALGO] Algo Sees Book as:\n" + book);

        var totalOrderCount = state.getChildOrders().size();

      

        final var activeOrders = state.getActiveChildOrders();

        if(totalOrderCount < 45){
            //logger and sys out will only show until TOC reaches required num
            BidLevel level = state.getBidAt(0);
            final long price = level.price;
            final long quantity = level.quantity;
            logger.info("[MYALGO] Adding order for" + quantity + "@" + price);
            System.out.println("total order count is " +totalOrderCount); 
            return new CreateChildOrder(Side.BUY, quantity, price);
        }
        if(activeOrders.size() > 5){
            final var option = activeOrders.stream().findFirst();
            // System.out.println("this is option "+option);
            // option is an object
            // System.out.println("total order count is" +totalOrderCount);
            // total num of orders place not num once they start cancelling. for that check active orders 

            if (option.isPresent()){
                
                // logger.info("[MYALGO] option is present total order count is" + totalOrderCount);
                // stays constant at totalOrder count

                // logger.info("[MYALGO] option is present, active orders are "+ activeOrders);
// shows order ids
                logger.info("[MYALGO] active orders are "+ activeOrders.size());
                // active orders is what we will track to cancel the orders
                
                // System.out.println("total order count is" +totalOrderCount);
                // System.out.println("active orders are " + state.getActiveChildOrders());
                var childOrder = option.get();
                // get the child order
                logger.info("[MYALGO] Cancelling order:" + childOrder);
                // logs info for child order
                logger.info("[MYALGO] option is present, number of active orders are "+ activeOrders.size());
                // logger.info("[MYALGO] option is present active orders are:" + activeOrders);
                // logger.info("[MYALGO] bolo total order count is now that we are cancelling is " +totalOrderCount);
                // logger.info("[MYALGO] child order to string"+childOrder.toString());
                // to string and deep to string do not work on childOrder, activeOrders or totalOrderCount. may work abother way not yet found
                
                return new CancelChildOrder(childOrder);
            }else{
                return NoAction.NoAction;
            }
        }else{
             logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 45, done.");
        // logs the order and what was returned
        return NoAction.NoAction;
        }

    }
}

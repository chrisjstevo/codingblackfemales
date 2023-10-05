package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);


        //If active orders > 1 : cancel

        final var activeOrders = state.getActiveChildOrders();

        if (activeOrders.size() > 1) {

            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[MYALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            }
            else{
                return NoAction.NoAction;
            }
        } else {
            
            final BidLevel nearTouch = state.getBidAt(0);
            long quantity = 90;
            long price = nearTouch.price;

                //until we have three child orders....
            if(state.getChildOrders().size() < 2){
                //then keep creating a new one
                logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 2, joining passive side of book with: " + quantity + " @ " + price);
                return new CreateChildOrder(Side.BUY, quantity, price);
            }else{
                logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 2, done.");
                return NoAction;
            }       
            
        }   

    }   
       
}

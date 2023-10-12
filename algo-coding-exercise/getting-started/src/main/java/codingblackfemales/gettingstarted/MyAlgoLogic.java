package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import java.util.Optional;

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
        final BidLevel nearTouch = state.getBidAt(0);

        long quantity = 75;
        long price = nearTouch.price;
        final var activeOrders = state.getActiveChildOrders();

        //until we have six child orders....
        if (state.getChildOrders().size() < 6) 
        {
        	// then keep creating a new one
        	logger.info("[MYALGO] Have:" + state.getChildOrders().size()
        			+ " children, want 6, joining passive side of book with: " + quantity + " @ " + price);
        	return new CreateChildOrder(Side.BUY, quantity, price);
        } 
        else 
        {
        	//Once six child orders have been created, start cancelling orders from the top of the book
        	logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 6, done.");
        	
        	final var option = activeOrders.stream().findFirst();
        	if (option.isPresent()) 
        	{
        		var childOrder = option.get();
        		logger.info("[ADDCANCELALGO] Cancelling order:" + childOrder);
        		return new CancelChildOrder(childOrder);
        	} 
        	else 
        	{
        		return NoAction.NoAction;
        	}
 
        }
        
	}	
}
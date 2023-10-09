package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AddCancelAlgoLogic;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;


public class MyAlgoLogic implements AlgoLogic {

   private int orderCount = 0;
   private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

   @Override
    public Action evaluate(SimpleAlgoState state) {
       

        logger.info("[MYALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[MYALGO] Algo Sees Book as:\n" + book);

        var totalOrderCount = state.getChildOrders().size();


        // exit condition to limit risk
         if (totalOrderCount > 10) {

            return NoAction.NoAction;
        }


        BidLevel BidLevel = state.getBidAt(0);
        long price = BidLevel.price; 
       
        if(state.getActiveChildOrders().isEmpty()) {

            // Create new buy order if none active
            long quantity = 100;
            orderCount++;
            logger.info("[MYALGO] Adding order if none active for" + quantity + "@" + price);
            return new CreateChildOrder(Side.BUY, quantity, price);

        } else if(orderCount < 3){

            // Create additional buy orders up to 3
            long quantity = 100;
            orderCount++;
            logger.info("[MYALGO] Adding order for" + quantity + "@" + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
      
          } else {
          
            // Cancel oldest buy order if at max
            ChildOrder oldestOrder = state.getActiveChildOrders().get(0);
            orderCount--;
            logger.info("[MYALGO] Cancelling order:" + oldestOrder);
            return new CancelChildOrder(oldestOrder); 
          }
      
    
    }
  
}


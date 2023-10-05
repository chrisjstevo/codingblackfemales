package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
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

        long totalTickPrice = 0; 
        long totalTickQuantity= 0; 

        for (int i = 0; i < state.getBidLevels(); i++) {
            BidLevel bidLevel = state.getBidAt(i);
            totalTickPrice += bidLevel.price;
            totalTickQuantity += bidLevel.quantity;
        }

        long averageTickPrice = totalTickPrice / state.getBidLevels(); 

        long quantity = 2000;
        long percentageOfMarketVolumeNotToExceed = 20;

            if (quantity > (totalTickQuantity * percentageOfMarketVolumeNotToExceed / 100)){
            logger.info("[MYALGO] Quantity of " + quantity + " exceeds 20% of the current market volume of" + totalTickQuantity);
            return NoAction.NoAction;
        }
        
       else if (quantity < (totalTickQuantity * percentageOfMarketVolumeNotToExceed / 100) && state.getChildOrders().size() < 3){
            
            logger.info("[MYALGO] Have: " + state.getChildOrders().size() + " children, want 3, joining BID side of the book with: " + quantity + " @ " + averageTickPrice);
            return new CreateChildOrder(Side.BUY, quantity, averageTickPrice);
        } else{
            logger.info("[MYALGO] Have: " + state.getChildOrders().size() + " children, want 3, all added to the order book.");
            return NoAction.NoAction;
    }
   
}
} 




    
        
        


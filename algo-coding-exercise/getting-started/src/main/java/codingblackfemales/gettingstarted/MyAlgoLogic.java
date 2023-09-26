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

        long totalTickPrice = 0; // Total price at all levels
        long totalTickVolume = 0; // Total volume at all bid levels 

        for (int i = 0; i < state.getBidLevels(); i++) {
            BidLevel bidLevel = state.getBidAt(i);
            totalTickPrice += bidLevel.price;
            totalTickVolume += bidLevel.quantity;
        }

        long averageTickPrice = totalTickPrice / state.getBidLevels(); 

        long quantity = 2000;

            if (quantity > (totalTickVolume / 5)){
            logger.info("[MYALGO] Quantity of " + quantity + " exceeds 20% of the current market volume");
            return NoAction.NoAction;
        }
        
       else if (quantity < (totalTickVolume / 5) && state.getChildOrders().size() < 3){
            
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + "children, want 3, joining BID side of the book with: " + quantity + " @ " + averageTickPrice);
            return new CreateChildOrder(Side.BUY, quantity, averageTickPrice);
        } else{
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 3, all added to the order book.");
            return NoAction.NoAction;
    }
   
}
}
// TO DO: 
// - can we get the top 10% of prices from the market data and then calculate the average? 
// - getBidLevels/ 10 
// -  loop until getBidLevels/10 
// - accumulator to caculate total amount 
// - divide by the number of of levels to calculate the top 10%

// old code - less efficient 
   //     final BidLevel bidAtIndexZero = state.getBidAt(0);
    //     long priceAtIndexZero = bidAtIndexZero.price; 
    //     long quantityAtIndexZero = bidAtIndexZero.quantity;

    //     final BidLevel bidAtIndexOne = state.getBidAt(1);
    //     long priceAtIndexOne = bidAtIndexOne.price;
    //     long quantityAtIndexOne = bidAtIndexOne.quantity;

    //     final BidLevel bidAtIndexTwo = state.getBidAt(2);
    //     long priceAtIndexTwo = bidAtIndexTwo.price;
    //     long quantityAtIndexTwo = bidAtIndexTwo.quantity;
        
    //     long quantity = 200;

    //     long averageTickPrice = (priceAtIndexZero + priceAtIndexOne + priceAtIndexTwo) / 3;
    //     long totalTickVolume = quantityAtIndexZero + quantityAtIndexOne + quantityAtIndexTwo; 

// REMINDER: write documentation for this algo exercise 
// can work on code base after submission to extend my knowledge and practice 
// complexity: looping through each price in the market then calculating an average price - done
// implementation: if price is lower then quantity is higher 
// can i get the overall amount of options? working on the top 10% of prices 
// make sure it is working first! - done



    
        
        


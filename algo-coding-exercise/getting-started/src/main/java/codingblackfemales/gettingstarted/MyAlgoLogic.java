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
//could do a TWAP algo - which purchases based off the average price with arket data ticks at 3 different levels 
// We want to be discrete in the order book and not effect the market or price too much - if the quantity we want to buy at that period is over 25% of the market volume then return no action
    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);
        
        final BidLevel bidAtIndexZero = state.getBidAt(0);
        long priceAtIndexZero = bidAtIndexZero.price; 
        long quantityAtIndexZero = bidAtIndexZero.quantity;

        final BidLevel bidAtIndexOne = state.getBidAt(1);
        long priceAtIndexOne = bidAtIndexOne.price;
         long quantityAtIndexOne = bidAtIndexZero.quantity;

        final BidLevel bidAtIndexTwo = state.getBidAt(2);
        long priceAtIndexTwo = bidAtIndexTwo.price;
         long quantityAtIndexTwo = bidAtIndexZero.quantity;
        
        //we want to be discrete so we don't buy too many at once
        long quantity = 200;

        long averageTickPrice = (priceAtIndexZero + priceAtIndexOne + priceAtIndexTwo) / 3;
        long totalTickVolume = quantityAtIndexZero + quantityAtIndexOne + quantityAtIndexTwo;

        if (quantity > (totalTickVolume * 0.2)){
            return NoAction.NoAction;
        }
        //until we have 5 orders...
        else if (state.getChildOrders().size() < 5){
            //create a new child order at this price and this quantity
            logger.info("[MYALGO] Have:" + state.getChildOrders() + "children, want 5, joining BID side of the book with: " + quantity + " @ " + averageTickPrice);
            return new CreateChildOrder(Side.BUY, quantity, averageTickPrice);
        } else{
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 5, all added to the order book.");
            return NoAction.NoAction;
        }
    }
}

// adding complexity - done!
// in this example, if its over 10% of the whole market then its not discrete anymore - dependendent on the client in a real life situation
// if (quantity > (averageTickVolume / 5)) at that specific time interval is greater than 25% of the total volume in the market 
// return no action 

//Things left to do
// 1. create various market ticks 
// 2. update createTickTwo
// 3. test these marketDataTicks

//homework: debug mode on all of the tests of passive algo test to further understand everything 

// question?
// clarify ask - so youre buying from the ask and selling to the Bid?
    
        
        


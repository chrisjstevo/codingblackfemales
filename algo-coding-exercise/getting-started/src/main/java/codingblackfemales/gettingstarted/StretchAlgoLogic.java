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

public class StretchAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(StretchAlgoLogic.class);
    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[STRETCHALGO] In Algo Logic....");

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[STRETCHALGO] The state of the order book is:\n" + orderBookAsString);

        //create a sell
        final AskLevel askLevel = state.getAskAt(0);

        long askPrice = askLevel.price;
        long askQuantity = askLevel.quantity;

        //ChildOrder newAskOrder = new ChildOrder(Side.BUY, 1, 50L, 105, 1);
        CreateChildOrder childOrder = new CreateChildOrder(Side.SELL, 50, 105);

        if(newAskOrder.getPrice() > askPrice){
            //match order
            //clear order how?
        }
        else{
            //do nothing
        }



        return NoAction.NoAction;
    }
}

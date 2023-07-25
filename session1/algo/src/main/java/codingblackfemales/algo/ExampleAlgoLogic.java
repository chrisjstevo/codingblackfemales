package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;

public class ExampleAlgoLogic implements AlgoLogic{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("In Algo Logic....");

        final BidLevel nearTouch = state.getBidAt(0);

        long quantity = 75;
        long price = nearTouch.price;

        //until we have three child orders....
        if(state.getChildOrders().size() < 3){
            //then keep creating a new one
            logger.info("Have:" + state.getChildOrders().size() + " children, want 3, carrying on...");
            return new CreateChildOrder(Side.BUY, quantity, price);
        }else{
            logger.info("Have:" + state.getChildOrders().size() + " children, want 3, all good.");
            return NoAction;
        }

    }
}

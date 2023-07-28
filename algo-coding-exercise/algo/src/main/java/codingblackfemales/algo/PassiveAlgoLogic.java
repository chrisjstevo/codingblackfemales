package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;

public class PassiveAlgoLogic implements AlgoLogic{

    private static final Logger logger = LoggerFactory.getLogger(PassiveAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[PASSIVEALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[PASSIVEALGO] Algo Sees Book as:\n" + book);

        final BidLevel nearTouch = state.getBidAt(0);

        long quantity = 75;
        long price = nearTouch.price;

        //until we have three child orders....
        if(state.getChildOrders().size() < 3){
            //then keep creating a new one
            logger.info("[PASSIVEALGO] Have:" + state.getChildOrders().size() + " children, want 3, joining passive side of book with: " + quantity + " @ " + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        }else{
            logger.info("[PASSIVEALGO] Have:" + state.getChildOrders().size() + " children, want 3, done.");
            return NoAction;
        }

    }
}

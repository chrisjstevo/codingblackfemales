package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.algo.PassiveAlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;

public class MyPassiveAlgoLogic  implements AlgoLogic
{
    private static final Logger logger = LoggerFactory.getLogger(MyPassiveAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[MYPASSIVEALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[MYPASSIVEALGO] Algo Sees Book as:\n" + book);

        final BidLevel nearTouch = state.getBidAt(0);

        long quantity = 75;
        long price = nearTouch.price;

        //until we have three child orders....
        if(state.getChildOrders().size() < 7){
            //then keep creating a new one
            logger.info("[MYPASSIVEALGO] Have:" + state.getChildOrders().size() + " children, want 7, joining passive side of book with: " + quantity + " @ " + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        }else{
            logger.info("[MYPASSIVEALGO] Have:" + state.getChildOrders().size() + " children, want 7, done.");
            return NoAction;
        }

    }
}

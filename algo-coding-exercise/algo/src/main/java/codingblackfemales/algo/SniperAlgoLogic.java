package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static codingblackfemales.action.NoAction.NoAction;

public class SniperAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(SniperAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[SNIPERALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[SNIPERALGO] Algo Sees Book as:\n" + book);

        final AskLevel farTouch = state.getAskAt(0);
        List<ChildOrder> childOrderList = state.getChildOrders();



        //take as much as we can from the far touch.... //market order
        long quantity = farTouch.quantity;
        long price = farTouch.price;

        //until we have three child orders....
        if (state.getChildOrders().size() < 5) {
            //then keep creating a new one
            for (int i = 0; i<state.getChildOrders().size(); i++) {
                logger.info("[SNIPERALGO]" + i + "=" + state.getChildOrders().get(i).toString());
            }
            logger.info("[SNIPERALGO] Have:" + state.getChildOrders().size() + " children, want 5, sniping far touch of book with: " + quantity + " @ " + price);

            return new CreateChildOrder(Side.BUY, quantity, price);
        } else {
            logger.info("[SNIPERALGO] Have:" + state.getChildOrders().size() + " children, want 5, done.");
            return NoAction;
        }
    }
}

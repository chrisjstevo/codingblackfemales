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

import static codingblackfemales.action.NoAction.NoAction;

public class SniperAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(SniperAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[SNIPERALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[SNIPERALGO] Algo Sees Book as:\n" + book);

        final AskLevel farTouch = state.getAskAt(0);//

        long numberOfBuyChildOrders = state.getChildOrders().stream().filter(order -> order.getSide() == Side.BUY).count();

        //take as much as we can from the far touch....
        long quantity = farTouch.quantity; // quantity is determined by the market
        long price = farTouch.price;//price is determined by the market

        //until we have three child orders....
        if (state.getChildOrders().size() < 5) {
           
            //then keep creating a new one
            logger.info("[SNIPERALGO] Have:" + state.getChildOrders().size() + " children, want 5, sniping far touch of book with: " + quantity + " @ " + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        } else {
            logger.info("[SNIPERALGO] Have:" + state.getChildOrders().size() + " children, want 5, done.");
            return NoAction;
        }
    }
}

// this algo is determined by the market
package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);
    private int ordersCreated = 0;

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel nearTouch = state.getBidAt(0);
        long quantity = 100;
        long price = nearTouch.price;

        //until we have three child orders....
        if(ordersCreated < 3 && state.getChildOrders().size() < 3){
            //then keep creating a new one
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 3, joining passive side of book with: " + quantity + " @ " + price);
            ordersCreated++;
            return new CreateChildOrder(Side.BUY, quantity, price);
        }else{
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 3, done.");
            return NoAction.NoAction;
        }

    }
}
*/
public class MyAlgoLogic implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {
        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);
        final BidLevel topBid = state.getBidAt(0);
        final AskLevel topAsk = state.getAskAt(0);
        if (state.getChildOrders().size() < 3) {
            logger.info("[MYALGO] creating child order.");
            return new CreateChildOrder(Side.BUY, 100, topBid.price);
        }
        long filledQuantity = state.getChildOrders().stream().mapToLong(ChildOrder::getFilledQuantity).sum();
        //if price is favourable place order
        if (topAsk.price <= topBid.price && filledQuantity < 600) {
            long quantityToFill = 600 - filledQuantity;
            logger.info("[MYALGO] Filling order.");
            return new CreateChildOrder(Side.SELL, quantityToFill, topAsk.price);
        }
        return NoAction.NoAction;
    }
}


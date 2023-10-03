package codingblackfemales.gettingstarted;

import codingblackfemales.container.Actioner;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messages.order.Side;

import static codingblackfemales.action.NoAction.NoAction;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        var totalOrderCount = state.getChildOrders().size();

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);


        final AskLevel farTouch = state.getAskAt(0);
        ;
        //NB - it seems that you can get the price or quantity using nearTouch.getPrice or nearTouch.getQuantity

        long farTouchQuantity = farTouch.quantity;
        long pricePaid = farTouch.price;
        long totalQuantityLimit = 300;

        //the point at which we make a profit
//        long profit =;

        //we want 3 child orders but we also have a limit to the quantity that we want to purchase
        if (totalOrderCount < 3 && farTouchQuantity < totalQuantityLimit) {
            long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();

            logger.info("[PASSIVEALGO] Have:" + state.getChildOrders().size() + " children, want 3, sniping at: " + farTouchQuantity + " @ " + pricePaid);
            return new CreateChildOrder(Side.BUY, farTouchQuantity, pricePaid);

        } else {
            return NoAction;
        }
    }
}





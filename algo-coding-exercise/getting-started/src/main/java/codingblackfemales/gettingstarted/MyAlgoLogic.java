package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
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

        //I opted for using a sniper algo for the situation where we think that the stock is severely undervalued - based on analysis technical analysis e.g. the moving average or based on fundamental analysis e.g. the P/E ratio

        //in this scenario we want to buy as much stock as possible at the undervalued price so that we can sell it and make a profit

        //algo step by step pseudo-code:

        //get the price and quantity at the farTouch price (for buying ie stock at a price that should immediately be fulfilled)

        //until we have 3 child orders keep buying at the fartouch price

        //once we have 3 child orders check the order book for the fartouch price

        //if the fartouch price (on the buyers side?) in the order book is more than the fartouch price that we bought it for ie compare the bidat(0) to the first askat price - ie the lowest price that we got it for then sell at the buyer nearTouch price?? or do we just sell at another price

        //the argument for selling at the buyers near touch is that the orders will be fulfilled immediately when the stock price goes to its normal price and so we can instantly make a profit and we should keep doing this until we run out of stock the more we do the more profit we will actually make
        //- would it be better to sell at the 3rd index ie the one with more stock and at a better price?

        var orderBookAsString = Util.orderBookToString(state);

        var totalOrderCount = state.getChildOrders().size();


        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        final AskLevel farTouch = state.getAskAt(0);

        long farTouchQuantity = farTouch.quantity;
        long farTouchPrice = farTouch.price;

        if (totalOrderCount < 3) {
            logger.info("[SINPERALGO] Have:" + state.getChildOrders().size() + "children, want 3, sniping far touch of book with: " + farTouchQuantity + "@" + farTouchPrice);
            return new CreateChildOrder(Side.BUY, farTouchQuantity, farTouchPrice);
        }
        if (totalOrderCount == 3) {
            //check the price of the order book on the bid side
            final BidLevel biddersNearTouch = state.getBidAt(0);

            //bidders near touch is our fartouch again - I decided to name the variables this way to make a differentiation between the two farTouch prices that we are using ie biddersNearTouch is the same as the sellersFarTouch
            long price = biddersNearTouch.price;
            long quantity = biddersNearTouch.quantity;

            //if the price which bidders are asking for stock is higher than what we initially paid we can sell as much as our stock as possible at a profit
            if (price > farTouchPrice) {
                return new CreateChildOrder(Side.SELL, price, quantity);
            }
        } else {
            return NoAction;
        }
        return NoAction;
    }
}



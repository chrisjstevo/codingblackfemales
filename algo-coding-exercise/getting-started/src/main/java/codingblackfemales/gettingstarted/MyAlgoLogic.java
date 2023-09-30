package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import static codingblackfemales.action.NoAction.NoAction;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MyAlgoLogic implements AlgoLogic {




    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    long bidPV = 0;
    long bidVol = 0;
    long askPV = 0;
    long askVol = 0;


    @Override
    public Action evaluate(SimpleAlgoState state) {


        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        int maxLevels = Math.max(state.getAskLevels(), state.getBidLevels());

        for (int i = 0; i < maxLevels; i++) {
            if (state.getBidLevels() > i) {
                BidLevel level = state.getBidAt(i);
                bidPV += level.getPrice() * level.getQuantity();
                bidVol += level.getQuantity();


            } else { logger.info("No BIDS found");}

            if (state.getAskLevels() > i) {
                AskLevel level = state.getAskAt(i);
                askPV += level.getPrice() * level.getQuantity();
                askVol += level.getQuantity();

            }
        }


        BidLevel nearTouch = state.getBidAt(0);
        long bidQuantity = nearTouch.quantity;
        long bidPrice = nearTouch.price;
        long bidVwap = bidPV / bidVol;

        AskLevel farTouch = state.getAskAt(0);
        //take as much as we can from the far touch....
        long askQuantity = farTouch.quantity;
        long askPrice = farTouch.price;
        long askVwap = askPV / askVol;

        if (state.getChildOrders().size() < 3) {
            for (int i = 0; i < maxLevels; i++) {
                if (askPrice > bidVwap) { return new NoAction();
                } else if (askPrice < bidVwap) {
                    return new CreateChildOrder(Side.BUY, bidQuantity, askPrice);
                }

                if (bidPrice < askVwap) {
                    return new NoAction();
                } else if (bidPrice > askVwap) {
                    return new CreateChildOrder(Side.SELL, askQuantity, bidPrice);}
            }
        } else {
            return NoAction;
        }
        return NoAction;

    }

}

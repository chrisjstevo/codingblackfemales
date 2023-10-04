package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import static codingblackfemales.action.NoAction.NoAction;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


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


        // Calculating the average prices on the order book i.e vwap for each side. In doing so more able to gauge whether order book is expensive or not.

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

        // The test only provides enough data for three orders, so we set the limit.


        if (state.getChildOrders().size() < 6) {
            // Then we set up the variables we need to loop through the levels comparing the prices of the bids and asks to the average and making trading decisions on that basis.

            for (int i = 0; i < maxLevels; i++) {
                List results = new ArrayList();

                long bidVwap = bidPV / bidVol;
                long askVwap = askPV / askVol;

                BidLevel nearTouch = state.getBidAt(i);
                long bidQuantity = nearTouch.quantity;
                long bidPrice = nearTouch.price;


                AskLevel farTouch = state.getAskAt(i);
                long askQuantity = farTouch.quantity;
                long askPrice = farTouch.price;

                // First with asks if the price is above buyer expectations then it may be overvalued so cancel any buy orders
                if (askPrice > bidVwap){
                    new CancelChildOrder(new ChildOrder(Side.BUY, 1, bidQuantity, bidPrice, i));
                    logger.info("[MY ALGO] Have: {} children, want 3, as ask price is higher than average buyer expectation cancel order for {} @ {}",state.getChildOrders().size(),bidQuantity, bidPrice );

                 // Then  if the price the instrument is selling at is less than the average buyer sentiment then create a buy order in the hopes of a bargain.
                } else if (askPrice < bidVwap) {
                   state.getChildOrders().add(new ChildOrder(Side.BUY,1, askQuantity, askPrice,i));
                    logger.info("[MY ALGO] Have: {} children, want 3, submitting bid order for {} @ {}",state.getChildOrders().size(),askQuantity,askPrice );

                 // With the bids we do the inverse. If the offer we're receiving is less than the average pricing by sellers then we believe it is undervalued and cancel orders to sell.

                } else if (bidPrice < askVwap){ new CancelChildOrder(new ChildOrder(Side.SELL, 1, askQuantity, askPrice, i));
                logger.info("[MY ALGO] Have: {} children, want 3, bid price is higher than lower than average seller expectation cancel order for {} @ {}",state.getChildOrders().size(),askQuantity,askPrice );

                // If the offer is above the general valuation by sellers then we sell in the hopes of making a profit.
                } else if (bidPrice >= askVwap) {
                    state.getChildOrders().add(new ChildOrder(Side.SELL,1, bidQuantity, bidPrice,i));
                    logger.info("[MY ALGO] Have: {} children, want 3, submitting bid order for {} @ {}",state.getChildOrders().size(),bidQuantity,bidPrice);
                }
            }
        } else {
            return NoAction;
        }
        return NoAction;

    }

}

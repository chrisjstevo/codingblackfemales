package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
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

        if (state.getChildOrders().size() > 0) {
            var order = state.getChildOrders().stream().findFirst().get();
            return new CancelChildOrder(order);

            // Set up for calculating the average prices on the order book i.e vwap for each side. In doing so more able to gauge whether order book is expensive or not.

        } else  {

            for (int i = 0; i < maxLevels; i++) {
                if (state.getBidLevels() > i) {
                    BidLevel level = state.getBidAt(i);
                    bidPV += level.getPrice() * level.getQuantity();
                    bidVol += level.getQuantity();

                } else { logger.info("No more BIDS found");}

                if (state.getAskLevels() > i ) {
                    AskLevel level = state.getAskAt(i);
                    askPV += level.getPrice() * level.getQuantity();
                    askVol += level.getQuantity();
                } else { logger.info("No more ASKs found");}

                // Then we set up the variables we need to loop through the levels comparing the prices of the bids and asks to the average and making trading decisions on that basis.

                long bidVwap = bidPV / bidVol;
                long askVwap = askPV / askVol;

                BidLevel nearTouch = state.getBidAt(i);
                AskLevel farTouch = state.getAskAt(i);

                long bidQuantity = nearTouch.quantity;
                long askQuantity = farTouch.quantity;

                long bidPrice = nearTouch.price;
                long askPrice = farTouch.price;

                // First with asks if the price is above market expectations then it may be overvalued so cancel any buy orders and sell more.

                if (askVwap > bidVwap) {
                    logger.info("[MY ALGO] Have: {} child order, want 3, book is pricier so submitting sell order for {} @ {}", state.getActiveChildOrders().size(), askQuantity, askPrice);
                    state.getChildOrders().add(new ChildOrder(Side.SELL, 1, askQuantity, askPrice, i));

                    // Then  if the price the instrument is selling at is less than the average buyer sentiment then create a buy order in the hopes of a bargain.
                } else if (askVwap < bidVwap) {
                    state.getChildOrders().add(new ChildOrder(Side.BUY, 1, bidQuantity, bidPrice, i));
                    logger.info("[MY ALGO] Have: {} child order, want 3, book is cheaper so submitting bid order for {} @ {}", state.getActiveChildOrders().size(), bidQuantity, bidPrice);
                }


            }




        }

          return NoAction;


    }


}



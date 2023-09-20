package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sotw.ChildFill;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        //get order-book state
        var orderBookAsString = Util.orderBookToString(state);
        logger.info("**[MYALGO]** The state of the order book is:\n" + orderBookAsString);

        //get the active orders
        var activeOrders = state.getActiveChildOrders();
        logger.info("**[MYALGO]** Active child orders are:  " + activeOrders);

        //create a 4 child orders //else cancel the oldest child order that is not filled
        if (activeOrders.size() < 4) {
//            //get the moving average price from the last 30s of top bid prices
//            Queue<Long> averagePriceList = new LinkedList<Long>();
//            long movingAverage = getMovingAverage(state, averagePriceList);
//            logger.info("**[MYALGO]** The averagePrice is: " + movingAverage + "\n\n");

            long[] newChildOrder = getNewMidChildOrder(state);

            logger.info("**[MYALGO]** Creating new childorder ");

            return new CreateChildOrder(Side.SELL, newChildOrder[0], newChildOrder[1]);
            //return new CreateChildOrder(Side.SELL, newChildOrder[0], newChildOrder[1]);
        } else {

            int filled_orders = 0;

            for (ChildOrder cf : state.getChildOrders()) {
                filled_orders += cf.getFilledQuantity();
            }
            logger.info("**[MYALGO]** Total Filled orders are " + filled_orders);
            logger.info("**[MYALGO]**  child orders are:  " + state.getChildOrders());
            logger.info("**[MYALGO]**  Active child orders are:  " + activeOrders);

            if (activeOrders.get(0).getFilledQuantity()>0) {
                logger.info("**[MYALGO]** cancelling child bid order with id: " + activeOrders.get(0).getOrderId());
                return new CancelChildOrder(activeOrders.get(0));
            } else {
                return new NoAction();
            }
        }

    }

    public static void wait(int ms) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //create another class to carry out the trading logic

    //get the top big price to calculate the moving average every 10 seconds for a minute
    public static long getMovingAverage(SimpleAlgoState state, Queue<Long> averagePriceList) {
        long movingAverage = 0;
        int timeIntervalInMs = 10000;
        int numOfTimeIntervals = 6;
        if (averagePriceList.size() == 0) {
            for (int i = 0; i < numOfTimeIntervals; i++) {
                averagePriceList.offer(state.getBidAt(0).price);
                wait(timeIntervalInMs);
            }
        } else {
            averagePriceList.poll();
            averagePriceList.offer(state.getBidAt(0).price);
        }
        for (long price : averagePriceList) {

            movingAverage += price;
        }
        movingAverage = movingAverage / numOfTimeIntervals;
        return movingAverage;
    }

    public static long[] getNewMidChildOrder(SimpleAlgoState state) {
        long[] midChildOrder = new long[2];

        BidLevel topBid = state.getBidAt(0);
        AskLevel topAsk = state.getAskAt(0);

        long totalQuantity = topAsk.quantity + topBid.quantity;
        long midQuantity = totalQuantity / 2;
        long midPrice = (topAsk.price * topAsk.quantity + topBid.price * topBid.quantity) / totalQuantity;

        midChildOrder[0] = midQuantity;
        midChildOrder[1] = midPrice;

        return midChildOrder;
    }

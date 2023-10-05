package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.marketdata.api.MarketDataMessage;
import codingblackfemales.marketdata.gen.MarketDataGenerator;
import codingblackfemales.marketdata.gen.SimpleFileMarketDataGenerator;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.gettingstarted.TimedLogic;
import codingblackfemales.util.Util;
import messages.order.Side;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {
    // Originally i had tried a method that bought until child orders reached 30 and then sold until we reached 

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);
    private Map<Long, Long> marketMonitor = new HashMap<>();

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        /********
         *
         * Add your logic here....
         *
         */

    // Reverting to original MyAlgoLogic that both sells and buys. 

        final String book = Util.orderBookToString(state);
        // sell logic
        final AskLevel farTouch = state.getAskAt(0);
        long quantitySELL = farTouch.quantity;
        long priceSELL = farTouch.price;
        // BUY Logic
        final BidLevel nearTouch = state.getBidAt(0);
        long quantityBuy = nearTouch.quantity;
        long priceBuy = nearTouch.price;

        logger.info("[MYALGO] Algo Sees Book as: \n" + book);
        var totalOrderCount = state.getChildOrders().size();
        var activeOrders = state.getActiveChildOrders();
        TimedLogic timedLogic = new TimedLogic(marketMonitor);
        
        

        // checks if market is open, this is left over from the timed logic that i was working on. if total orders are less than 15 we buy, if order count is divisible by 3 we sell and if active orders are greater than 6 we cancel.
        if(timedLogic.isMarketOpen()){
            if(totalOrderCount < 15 ){

                logger.info("[MYALGO] Aftr buy orders" + activeOrders.size());
                logger.info("[MYALGO] After Buy total orders" + totalOrderCount);
                            return new CreateChildOrder(Side.BUY, quantityBuy, priceBuy);
            }
            
            if(totalOrderCount % 3 == 0){
 
                logger.info("[MYALGO] Adding order for" + quantitySELL + "@" + priceSELL);
                logger.info("[MYALGO] Initial quantity" + quantitySELL + " price " + priceSELL);
                logger.info("[MYALGO] SLL active orders" + activeOrders.size());
                logger.info("[MYALGO] total orders" + totalOrderCount);

                return new CreateChildOrder(Side.SELL, quantitySELL, priceSELL);
            }

             if(activeOrders.size() > 6){
            final var option = activeOrders.stream().findFirst();
            // option is an object
             logger.info("[MYALGO] CNX Have:" + activeOrders.size() + " children, want 7, done.");
            if (option.isPresent()){
                logger.info("[MYALGO] active orders are "+ activeOrders.size());
                // active orders is what we will track to cancel the orders
                var childOrder = option.get();
                // get the child order
                logger.info("[MYALGO] Cancelling order:" + childOrder);
                // logs info for child order
                logger.info("[MYALGO] option is present, number of active orders are "+ activeOrders.size());
                return new CancelChildOrder(childOrder);
            }else{
                // this else is causing failure
                return NoAction.NoAction;
            }
 
        }else{
            return NoAction.NoAction;
        }
            
        } else{
            return NoAction.NoAction;
        }
    }


    // I was working on a timed logic as below most of its methodology is housed in TimedLogic.java. Unfortunatley i was unable to get it to run, i have left it cemented out below and in TimedLogic.java
    //     TimedLogic timedLogic = new TimedLogic(marketMonitor);
    //     final String book = Util.orderBookToString(state);
    //     final AskLevel farTouch = state.getAskAt(0);
    //     logger.info("[MYALGO] Algo Sees Book as: \n" + book);
    //     var totalOrderCount = state.getChildOrders().size();
    //     var activeOrders = state.getActiveChildOrders();
        
    //     //take as much as we can from the far touch....
    //     long quantity = farTouch.quantity;
    //     long price = farTouch.price;

    //     logger.info("[MYALGO] Initial quantity" + quantity + " price " + price);

    //     if(timedLogic.isMarketOpen()){
    //         if(totalOrderCount < 1175){
    //             timedLogic.scheduleDataCollection(farTouch);
    //             logger.info("[MYALGO] current child orders are " + totalOrderCount);
    //             logger.info("[MYALGO] total active orders" + activeOrders.size());
    //         }            

    //             if(timedLogic.getMarketMonitor().size() > 2000){
    //                         final var option = activeOrders.stream().findFirst();
    //         // option is an object
    //          logger.info("[MYALGO] Have:" + activeOrders.size() + " children, want 5, done.");
    //         if (option.isPresent()){
    //             logger.info("[MYALGO] active orders are "+ activeOrders.size());
    //             // active orders is what we will track to cancel the orders
    //             var childOrder = option.get();
    //             // get the child order
    //             logger.info("[MYALGO] Cancelling order:" + childOrder);
    //             // logs info for child order
    //             logger.info("[MYALGO] option is present, number of active orders are "+ activeOrders.size());
    //             return new CancelChildOrder(childOrder);
    //         }else{
    //             // this else is causing failure
    //             return NoAction.NoAction;
    //         }
    //     }else{
    //         return NoAction.NoAction;
    //             }
            
    //     } 
    // }
    }

// package codingblackfemales.gettingstarted;

// import codingblackfemales.action.Action;
// import codingblackfemales.action.CancelChildOrder;
// import codingblackfemales.action.CreateChildOrder;
// import codingblackfemales.action.NoAction;
// import codingblackfemales.algo.AlgoLogic;
// import codingblackfemales.sotw.SimpleAlgoState;
// import codingblackfemales.util.Util;
// import messages.order.Side;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import codingblackfemales.sotw.marketdata.BidLevel;

// public class MyAlgoLogic implements AlgoLogic {

//     private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

//     @Override
//     public Action evaluate(SimpleAlgoState state) {

//         var orderBookAsString = Util.orderBookToString(state);

//         logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

//         // var childOrders = state.getChildOrders();
//         final var activeChildOrders = state.getActiveChildOrders();
        
//         logger.info("[MYALGO] The number of active child orders is: " + activeChildOrders.size());
//         // logger.info("[MYALGO] The filled child orders: " );
        
//         if(activeChildOrders.size() < 3){
//             logger.info("[MYALGO] No child orders exist. Start by creating new order");
//             BidLevel level = state.getBidAt(0);
//             var price = level.price;
//             // var quantity = level.quantity;
//             var quantity = 75;
//             logger.info("[MYALGO] bid level is: " + level);
//             logger.info("[MYALGO] bid price: " + price);
//             return new CreateChildOrder(Side.BUY, quantity, price); 
//         }else{
//             final var option = activeChildOrders.stream().findFirst();

//             if(option.isPresent()){
//                 var childOrderOption = option.get();
//                 BidLevel bestBidLevel = state.getBidAt(0);
//                 // var childOrderPrice = childOrderOption.getPrice();

//                 if(bestBidLevel == null){
//                     logger.info("[MYALGO] No bid levels exist. Cancel the order");
//                     return NoAction.NoAction;
//                 }

//                 if(childOrderOption.getPrice() == bestBidLevel.price){
//                     logger.info("[MYALGO] Active child order price is still valid. Do nothing");
//                     return NoAction.NoAction;
//                 }
                
//                 logger.info("[MYALGO] Active child order is (option): " + childOrderOption);
//                 return new CancelChildOrder(childOrderOption);
//             }
//         }
//         return NoAction.NoAction;
//     }
// }


package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.util.Util;
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import codingblackfemales.sotw.marketdata.BidLevel;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[MYALGO] The state of the order book is:\n"
                + Util.orderBookToString(state));

        final var activeChildOrders = state.getActiveChildOrders();

        logger.info("[MYALGO] Active child orders: " + activeChildOrders.size());

        BidLevel bestBidLevel = state.getBidAt(0);

        // edge case: no bid levels exist
        if (bestBidLevel == null) {
            logger.info("[MYALGO] No bid levels exist. No action.");
            return NoAction.NoAction;
        }

        // create orders
        if (activeChildOrders.size() < 3) {

            long price = bestBidLevel.price;
            // long quantity = bestBidLevel.quantity;
            long quantity = 75;

            logger.info("[MYALGO] Creating BUY order: "
                    + quantity + " @ " + price);

            return new CreateChildOrder(Side.BUY, quantity, price );
        }

        // CANCEL ORDER
        final var option = activeChildOrders.stream().findFirst();

        if (option.isEmpty()) {
            return NoAction.NoAction;
        }

        final var childOrder = option.get();

        if (childOrder.getPrice() == bestBidLevel.price) {

            logger.info("[MYALGO] Child order is still at the best bid. No action.");

            return NoAction.NoAction;
        }

        logger.info("[MYALGO] Best bid has changed. Cancelling stale child order: " + childOrder);
        

        return new CancelChildOrder(childOrder);
    }
}




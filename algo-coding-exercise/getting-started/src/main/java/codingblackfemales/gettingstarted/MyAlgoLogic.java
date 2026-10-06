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
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    // Maximum order quantity
    private static final long MAX_ORDER_QUANTITY = 100;

    /* 
    * STRETCH GOAL VARIABLES
    * These variables are only used by the stretch-goal logic
    * The idea of the stretch goal is to look at several market observations,
    * calculate a reference price, and then buy when the current ask price 
    * becomes sufficiently cheaper than that reference price
    */

    // The stretch goal uses a 5% discount from the calculated reference price
    // as the point at which we are willing to buy
    // Example:
    // Reference price = 98
    // 5% discount = 98 * 95 / 100 = 93
    // Therefore, we buy if the ask price reaches 93 or lower
    private static final long BUY_DISCOUNT_PERCENT = 5;

    // The stretch goal uses a 5% premium above the reference price
    // as the point at which we are willing to sell
    private static final long SELL_PREMIUM_PERCENT = 5;

    // Counts how many times evaluateStretch() has been called.
    // In the current implementation this is being used as a simple way
    // of counting market observations/ticks.
    private int tickCount = 0;

    // Stores the sum of the ask prices observed during the first
    // three observations.
    // This is later divided by 3 to calculate the reference price
    private long totalAskPrice = 0;
    
    // The average ask price calculated from the first three observations
    // Example:
    // (100 + 98 + 97) / 3 = 98
    private long referencePrice = 0;

    // The price at which the stretch goal will consider buying
    // This is calculated as 95% of the reference price because
    // BUY_DISCOUNT_PERCENT is currently 5%.
    private long buyThreshold = 0;

    // The price at which the stretch goal will consider selling
    // This is calculated as 105% of the reference price
    private long sellThreshold = 0;

    // Stores the price paid when the stretch algo buys shares
    private long entryPrice = 0;

    // Stores the quantity bought so the same quantity can be sold later
    private long entryQuantity = 0; 

    // Prevents the stretch algo from creating the same BUY order repeatedly
    // once the buying condition has been met
    private boolean buyOrderCreated = false;

    // Originally intended to track whether a stretch BUY was waiting
    // to be processed by the framework
    // private boolean stretchBuyPending = false;

    // Determines which version of the algorithm should run
    // false = normal/main assessment algorithm
    // true  = stretch-goal algorithm
    private final boolean stretchMode;

    public MyAlgoLogic() {
        this(false);
    }

    public MyAlgoLogic(boolean stretchMode) {
        this.stretchMode = stretchMode;
    }

    @Override
    public Action evaluate(SimpleAlgoState state) {

         /*
         * The framework calls evaluate() when the algorithm needs
         * to decide what action to take
         *
         * If stretchMode is true, use the experimental stretch logic
         * Otherwise, use the main assessment logic
         */
       
        if (stretchMode) {
            return evaluateStretch(state);
        }

        return evaluateMain(state);
    }

    /*
    * MAIN ALGO ASSESSMENT
    *
    * The main algorithm follows the best bid
    * 
    * If there is no active order:
    *     -> create a BUY at the current best bid
    *
    * If there is already an active order:
    *     -> if the best bid has not changed, do nothing
    *     -> if the best bid has changed, cancel the old order
    *
    * After the cancellation, the algorithm can be evaluated again
    * and create a new order at the new best bid
    */
    
    private Action evaluateMain(SimpleAlgoState state) {
        
        logger.info("[MYALGO] The state of the order book is:\n {}", Util.orderBookToString(state));

        // Get the total number of child orders that have ever been created
        // This is different from getActiveChildOrders()
        // Cancelled orders are still included in getChildOrders()
        var totalOrders = state.getChildOrders().size();


        // Safety exit condition 
        // If the algorithm has already created 10 child orders, stop creating any more
        // This prevents an accidental loop where the algo could continuously cancel and recreate orders
        if (totalOrders >= 10) {
            return NoAction.NoAction;
        }

        // Get all child orders that are currently active
        // Cancelled orders are not included here
        final var activeOrders = state.getActiveChildOrders();

        // CASE 1: THERE IS ALREADY AN ACTIVE ORDER

        // If there is an active order, check whether the best bid has changed
        if (!activeOrders.isEmpty()) {

            // Get the first active child order
            final var option = activeOrders.stream().findFirst();

            // In a real trading system, order creation and cancellation may not happen immediately
            // The market can move while an order is still pending, which could result in multiple
            // outstanding orders if the algo reacts to every market-data update
            if (option.isPresent()) {
                var childOrder = option.get();
               
                // Get the current best bid from the order book
                // Index 0 represents the best/highest bid level
                BidLevel level = state.getBidAt(0);

                // If there is no bid in the order book, there is nothing for the algorithm to react to
                if (level == null) {
                     logger.info("No bid available: no action required");
                    return NoAction.NoAction;
                }
                final long price = level.price;

                
                // Compare the price of our existing order with the current best bid
                // If they are equal, our order is already sitting at the correct price
                // Therefore, there is no reason to cancel and recreate it
                if (childOrder.getPrice() == price) {
                    logger.info("No action required: active order already at " + price);
                    return NoAction.NoAction;
                }

                // If the best bid has changed, cancel the existing order
                // The algo can then be evaluated again and create a new order at the new best bid
                logger.info("Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            }
            else {
                // No active order was found, so take no action
                return NoAction.NoAction;
            }
         } else {
            // CASE 2: THERE IS NO ACTIVE ORDER

            // We now look for the current best bid so that we can create a new BUY order
            BidLevel level = state.getBidAt(0);

            // If there is no bid available, we cannot create a BUY order
            if (level == null) {
                logger.info("No bid available: no action required");
                return NoAction.NoAction;
            }

            logger.info("Best bid price:" + level.price);
            logger.info("Best bid quantity:" + level.quantity);

             // Store the current best bid price
            final long price = level.price;

            // Limit each child order to a maximum of 100 shares
            // If fewer than 100 shares are available, use the available quantity
            final long quantity = Math.min(level.quantity, MAX_ORDER_QUANTITY);

            logger.info("Adding order for" + quantity + "@" + price);

            // Create a BUY child order at the current best bid
            // Side.BUY -> we want to buy
            // quantity  -> maximum 100 shares
            // price     -> current best bid
            return new CreateChildOrder(Side.BUY, quantity, price);

         }

        }

        // STRETCH GOAL

        /*
        * The basic idea is:
        *
        * 1. Observe the market several times
        * 2. Calculate an average/reference price
        * 3. BUY when the market price is sufficiently below the reference price
        * 4. SELL when the market price is sufficiently above the reference price
        */

         private Action evaluateStretch(SimpleAlgoState state) {
            
            // Increase the observation counter every time this method is evaluated
            tickCount++;
            
            // Get the current best ask
            // The ask is the cheapest price currently available from someone willing to sell
            AskLevel askLevel = state.getAskAt(0);
            
            // If there is no ask, we cannot calculate a buying opportunity
            if (askLevel == null) {
                return NoAction.NoAction;
            }

            // Add the current ask price to our running total
            // The first three observations are used to calculate the reference price
            totalAskPrice += askLevel.price;
            
            // Once we have collected three observations, calculate the average ask price
            // This becomes our reference price
            if (tickCount == 3) {

                referencePrice = totalAskPrice / 3;

                // Calculate the price we are willing to pay
                buyThreshold = referencePrice * (100 - BUY_DISCOUNT_PERCENT) / 100;

                // Calculate the price at which we are willing to sell
                sellThreshold = referencePrice * (100 + SELL_PREMIUM_PERCENT) / 100;
            }

            // Only consider buying after the reference price has been calculated
            // tickCount > 3 means we are now looking at observations after the initial three used to build the reference
            // !buyOrderCreated prevents the algo from creating another BUY every time evaluateStretch() is called after the threshold has been reached
            // askLevel.price <= buyThreshold means: The current ask is at or below the price we decided was cheap enough to buy
            if (!buyOrderCreated && tickCount > 3
                && askLevel.price <= buyThreshold) {

                // Limit the stretch-goal order to the same maximum quantity as the main algorithm
                long quantity = Math.min(askLevel.quantity, MAX_ORDER_QUANTITY);

            // Record the BUY so we know what price and quantity to sell later.
            entryPrice = askLevel.price;
            entryQuantity = quantity;

            // Prevent the algo from creating another BUY before selling the current position
            buyOrderCreated = true;

            // Create the BUY at the current ask price
            return new CreateChildOrder(
                    Side.BUY,
                    quantity,
                    askLevel.price
            );

        }

           // Only consider selling after the stretch algo has bought shares
            if (buyOrderCreated && entryQuantity > 0
                && askLevel.price >= sellThreshold) {

                // Reset the position so the algo can look for another BUY opportunity
                buyOrderCreated = false;
                entryPrice = 0;

                 // Store the quantity being sold before resetting it
                long quantityToSell = entryQuantity;
                entryQuantity = 0;
                
                // Create the SELL at the current ask price
                return new CreateChildOrder(Side.SELL, quantityToSell, askLevel.price);
            } 

        // If neither the buying nor selling conditions have been met, do nothing
        return NoAction.NoAction;
    
    }
}

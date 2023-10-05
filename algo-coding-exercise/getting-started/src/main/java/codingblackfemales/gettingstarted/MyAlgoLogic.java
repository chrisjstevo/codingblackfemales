package codingblackfemales.gettingstarted;


import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messages.order.Side;

import java.util.ArrayList;
import java.util.List;

import static codingblackfemales.action.NoAction.NoAction;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    private final List<Long> listOfOrders = new ArrayList<>();

    /**
     * Context for my Algo - I have created an Algo, which waits for a specific stock to reduce in value before buying.
     * Once bought it then waits until the stock hits a certain value where we can sell all of our stock to make a profit.
     * This could be used in the situation where we have done fundamental or technical analysis and the value of the stock is trending downwards but we (based on our analysis) believe that it will eventually trend upwards
     * Therefore we are purchasing while the value of the stock is low
     * Once the value of the stock reflects that which we expect from our analyses - we sell at a profit
     */

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        var totalOrderCount = state.getChildOrders().size();


        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);


        //This is the buying and cancelling logic of the Algo:
        final BidLevel nearTouch = state.getBidAt(0);

        long quantity = 100;
        long price = nearTouch.price;

        long filledQuantity;

        //we want to generate 3 child orders
        if (totalOrderCount < 3) {
            logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 3, adding passive order to the book at: " + quantity + " @ " + price);

            //This adds the quantity and price of each child order to the Arraylist - so the data can be used in the weighed price calculation
            listOfOrders.add(quantity);
            listOfOrders.add(price);

            return new CreateChildOrder(Side.BUY, quantity, price);

        }

        final var activeOrders = state.getActiveChildOrders();

        filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();

        if (activeOrders.size() == 3 && totalOrderCount == 3) {
            logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);
            final var option = activeOrders.stream().findFirst();

            //We want to cancel a childOrder so that we only have 2 active child orders
            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[MYALGO] Now only want 2 children, cancelling order: " + childOrder);

                //Delete the quantity of the first childOrder in the arrayList so that the ArrayList will only contain data from the active child orders
                int firstChildOrderQuantityIndex = 0;
                listOfOrders.remove(firstChildOrderQuantityIndex);

                //Delete the price of the first childOrder in the arrayList (the index of the price is 0 because the order of the list changes after the quantity is deleted)
                int firstChildOrderPriceIndex = 0;
                listOfOrders.remove(firstChildOrderPriceIndex);

                //delete the first active childOrder
                return new CancelChildOrder(childOrder);
            }


        } else if (activeOrders.size() == 2 && filledQuantity == (quantity * activeOrders.size())) {
            //This is the selling logic of the ALgo:
            //The above logic checks that we have the correct number of activeOrders and the filledQuantity is the maximum that we want before trying to sell

            //Logic to get the best bid in the order book
            final BidLevel farTouch = state.getBidAt(0);
            long bestBidPrice = farTouch.price;
            long bestBidQuantity = farTouch.quantity;

            //Logic to get the next best bid in the order book
            final BidLevel nextBest = state.getBidAt(1);
            long nextBestPrice = nextBest.price;
            long nextBestQuantity = nextBest.quantity;

            //Logic to get the worst bid in the order book
            int levels = state.getBidLevels();
            final BidLevel worstBid = state.getBidAt(levels - 1);
            long worstBidPrice = worstBid.price;
            long worstBidQuantity = worstBid.quantity;

            //Variables storing quantity and price for the childOrders that we filled so that we can use them in the weightedPrice calculation
            long firstChildOrderQuantity = listOfOrders.get(0);
            long firstChildOrderPrice = listOfOrders.get(1);

            long secondChildOrderQuantity = listOfOrders.get(2);
            long secondChildOrderPrice = listOfOrders.get(3);

            //WeightedPricePaid = ((Q1 x P1) + (Q2 * P2)) / (Q1 + Q2)
            long weightedPricePaid = ((firstChildOrderQuantity * firstChildOrderPrice) + (secondChildOrderQuantity * secondChildOrderPrice)) / (firstChildOrderQuantity + secondChildOrderQuantity);

            long profit = bestBidPrice - weightedPricePaid;


            if (profit > 0) {
                if (bestBidQuantity >= filledQuantity) {
                    //Sell all our stock at the best price
                    logger.info("[MYALGO] Can sell at a profit selling: " + bestBidQuantity + " @" + bestBidPrice);
                    logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);
                    return new CreateChildOrder(Side.SELL, filledQuantity, bestBidPrice);

                } else if (nextBestQuantity >= filledQuantity && nextBestPrice > weightedPricePaid) {
                    //Sell all our stock at the next best price that we can make a profit if the best price doesn't have enough quantity
                    logger.info("[MYALGO] Can sell at a profit selling: " + filledQuantity + " @" + nextBestPrice);
                    return new CreateChildOrder(Side.SELL, filledQuantity, nextBestPrice);

                } else if (worstBidQuantity >= filledQuantity && worstBidPrice > weightedPricePaid) {
                    //Sell all our stock at the worst price at which we still make profit if the next best price doesn't have enough quantity
                    logger.info("[MYALGO] Can sell total stock at a (reduced) profit, selling " + filledQuantity + " @ " + worstBidPrice);

                    logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);

                    return new CreateChildOrder(Side.SELL, filledQuantity, worstBidPrice);
                }

            } else if (profit < -10 && bestBidQuantity >= filledQuantity) {
                logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);
                //-10 is the absolute lowest we are willing to hold our stock at - at this price our position has changed and so now we want to cut our losses and sell all our stock
                logger.info("[MYALGO] Preventing further loss, I am selling all the stock and adding an ask to the book with: " + filledQuantity + " @ " + bestBidPrice);
                return new CreateChildOrder(Side.SELL, filledQuantity, bestBidPrice);

            } else if (profit < -10 && bestBidQuantity < filledQuantity && worstBidQuantity >= filledQuantity){
                logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);
                //We sell all of our stock at the absolute worst price just so that we can cut our losses
                logger.info("[MYALGO] Preventing further loss, I am selling all the stock and adding an ask to the book with: " + filledQuantity + " @ " + worstBidPrice);
                return new CreateChildOrder(Side.SELL, filledQuantity, worstBidPrice);

            } else {
                //As long as the profit loss is less than -10 we hold our position until the stock goes up which is what we expect according to our analysis
                logger.info("[MYALGO] Potential to make a profit is present I am holding");
            }

        } else if (activeOrders.size() == 3) {
            //Once we have put in our sell order we have 3 active childOrders and so are done.
            logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);
            logger.info("[MYALGO] Placed 3 active orders (including the sell order). Done");
            return NoAction;
        }

        return NoAction;
    }
}


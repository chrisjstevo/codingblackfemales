package codingblackfemales.gettingstarted;


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

import java.util.ArrayList;
import java.util.List;

import static codingblackfemales.action.NoAction.NoAction;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    private final List<Long> listOfOrders = new ArrayList<>();

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        var totalOrderCount = state.getChildOrders().size();


        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);


        final BidLevel nearTouch = state.getBidAt(0);

        long quantityBought = 100;
        long pricePaid = nearTouch.price;

        long filledQuantity;

        //we want to generate 3 child orders
        if (totalOrderCount < 3) {
            logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 3, adding passive order to the book at: " + quantityBought+ " @ " + pricePaid);

            listOfOrders.add(quantityBought);
            listOfOrders.add(pricePaid);

            return new CreateChildOrder(Side.BUY, quantityBought, pricePaid);

        }


        final var activeOrders = state.getActiveChildOrders();

        filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();;

        if(activeOrders.size() == 3 && totalOrderCount == 3) {
            logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);
            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[MYALGO] Now only want 2 children, cancelling order: " + childOrder);

                //delete the quantity of the first childOrder in the arrayList
                int firstChildOrderQuantityIndex = 0;
                listOfOrders.remove(firstChildOrderQuantityIndex);

                //delete the price of the first childOrder in the arrayList
                //the index of the price is 0 because the size of the list changes after the quantity is deleted and so now the price index of the first child order is 0
                int firstChildOrderPriceIndex = 0;
                listOfOrders.remove(firstChildOrderPriceIndex);

                //delete the quantity and price of the first childOrder
                return new CancelChildOrder(childOrder);
            }

        } else if(activeOrders.size() == 2 && filledQuantity == (quantityBought * activeOrders.size())){
            //this checks that we have the correct number of activeOrders and the filledQuantity is the maximum that we want before trying to sell
            logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);


            //variables storing quantity and price for the childOrders that we filled so that we can use them in the weightedPrice equation
            long quantity1 = listOfOrders.get(0);
            long price1 = listOfOrders.get(1);

            long quantity2 = listOfOrders.get(2);
            long price2 = listOfOrders.get(3);


            //weightedPricePaid = ((Q1 x P1) + (Q2 * P2)) / (Q1 + Q2)
            long weightedPricePaid = ((quantity1 * price1) + (quantity2 * price2)) / (quantity1 + quantity2);

            final BidLevel farTouch = state.getBidAt(0);

            long bestBidPrice = farTouch.price;
            long bestBidQuantity = farTouch.quantity;

            long profit = bestBidPrice - weightedPricePaid;

            if(profit > 0 && bestBidQuantity >= filledQuantity){
                logger.info("[MYALGO] Can sell at a profit selling: " + bestBidQuantity + " @" + bestBidPrice);
                return new CreateChildOrder(Side.SELL, bestBidQuantity, bestBidPrice);

            } else if(profit < -10 && bestBidQuantity >= filledQuantity){
                logger.info("[MYALGO] Algo Sees Book as:\n" + orderBookAsString);
//              -10 is the absolute lowest we are willing to hold our stock at - at this price our position has changed and we want to cut our losses
                logger.info("[MYALGO] Preventing further loss, I am selling all the stock and adding an ask to the book with: " + filledQuantity + " @ " + bestBidPrice);;
                return new CreateChildOrder(Side.SELL, filledQuantity, bestBidPrice);

            } else {
                //as long as the profit loss is less than -10
                //we hold our position until the stock goes up which is what we expect according to our analysis
                logger.info("[MYALGO] Potential to make a profit is present I am holding");
                return NoAction;
            }

        }

        return NoAction;

        }
    }




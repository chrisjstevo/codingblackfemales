package codingblackfemales.gettingstarted;

import codingblackfemales.container.Actioner;

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

import static codingblackfemales.action.NoAction.NoAction;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);


    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        var totalOrderCount = state.getChildOrders().size();

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);


        final AskLevel farTouch = state.getAskAt(0);
        ;
        //NB - it seems that you can get the price or quantity using nearTouch.getPrice or nearTouch.getQuantity

        long quantity = farTouch.quantity;
        final long pricePaid = farTouch.price;
        long quantityLimit = 300;

        //the point at which we make a profit
//        long profit =;

        //we want 3 child orders but we also have a limit to the quantity that we want to purchase
        if (totalOrderCount < 3 && quantity <= quantityLimit) {
            logger.info("[PASSIVEALGO] Have:" + state.getChildOrders().size() + " children, want 3, sniping at: " + quantity + " @ " + pricePaid);
            return new CreateChildOrder(Side.BUY, quantity, pricePaid);
        }

        //if we buy 300 at each child order, our maximal quantity is 900

        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();

////        At this point we want to look to sell what we have purchased and so now the best bid price is our farTouch price
        if (totalOrderCount == 3) {
            final BidLevel bestBid = state.getBidAt(0);

            long bestBidPrice = bestBid.price;
            long bestBidQuantity = bestBid.quantity;
            long worstPricePaid = farTouch.getPrice();

            //profit is equal to the best bid price available - the price that we paid
            long profit = (bestBidPrice * bestBidQuantity) - (worstPricePaid * filledQuantity);

            //if we can make a profit and we have enough stock to satisfy the order then sell!!

            System.out.println(pricePaid);

            if (profit > 0 && filledQuantity <= bestBidQuantity) {
                logger.info("[PASSIVEALGO] Have found a profit I am selling");
                return new CreateChildOrder(Side.SELL, bestBidQuantity, bestBidPrice);

            } else if (profit < -10 && filledQuantity <= bestBidQuantity) {
                //-10 is the absolute lowest we are willing to hold our stock at - at this price our position has changed and we want to cut our losses
                logger.info("[PASSIVEALGO] Preventing further loss, I am selling stock");
                return new CreateChildOrder(Side.SELL, bestBidQuantity, bestBidPrice);

            } else {
                logger.info("[PASSIVEALGO] Potential to make a profit is still present I am holding");
                return NoAction;
                //as long as the profit loss is less than -10
                //we hold our position until the stock goes up which is what we expect according to our analysis
                }
            }
//
            return null;

        }


}


    //the argument for selling at the buyers near touch is that the orders will be fulfilled immediately when the stock price goes to its normal price and so we can instantly make a profit and we should keep doing this until we run out of stock the more we do the more profit we will actually make
    //- would it be better to sell at the 3rd index ie the one with more stock and at a better price?

//    var orderBookAsString = Util.orderBookToString(state);
//
//    var totalOrderCount = state.getChildOrders().size();
//
//
//        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);
//
//final AskLevel farTouch = state.getAskAt(0);
//
//        long farTouchQuantity = farTouch.quantity;
//        long farTouchPrice = farTouch.price;
//
//        if (totalOrderCount < 3) {
//        logger.info("[SINPERALGO] Have:" + state.getChildOrders().size() + "children, want 3, sniping far touch of book with: " + farTouchQuantity + "@" + farTouchPrice);
//        return new CreateChildOrder(Side.BUY, farTouchQuantity, farTouchPrice);
//        }
//        if (totalOrderCount == 3) {
////check the price of the order book on the bid side
//final BidLevel biddersNearTouch = state.getBidAt(0);
//
//        //bidders near touch is our fartouch again - I decided to name the variables this way to make a differentiation between the two farTouch prices that we are using ie biddersNearTouch is the same as the sellersFarTouch
//        long price = biddersNearTouch.price;
//        long quantity = biddersNearTouch.quantity;
//
//        //if the price which bidders are asking for stock is higher than what we initially paid we can sell as much as our stock as possible at a profit
//        if (price > farTouchPrice) {
//        return new CreateChildOrder(Side.SELL, price, quantity);
//        }
//        } else {
//        return NoAction;
//        }
//        return NoAction;
//        }
//        }

//    public Action evaluate(SimpleAlgoState state) {
//
//        var orderBookAsString = Util.orderBookToString(state);
//
//        var totalOrderCount = state.getChildOrders().size();
//
//        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);
//
//
//        final BidLevel nearTouch = state.getBidAt(0);
//        ;
//        //NB - it seems that you can get the price or quantity using nearTouch.getPrice or nearTouch.getQuantity
//
//        long quantity = 100;
//        long pricePaid = 98;
//
//        //the point at which we make a profit
////        long profit =;
//
//        //we want 3 child order
//        if (totalOrderCount < 3) {
//            logger.info("[PASSIVEALGO] Have:" + state.getChildOrders().size() + " children, want 3, joining passive side of book with: " + quantity + " @ " + pricePaid);
//            return new CreateChildOrder(Side.BUY, quantity, pricePaid);
//        }
//
//
//        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
//
//
//
////        At this point we want to look to sell what we have purchased and so now the best bid price is our farTouch price
//        if (filledQuantity == 300 && filledQuantity < 600) {
//            final BidLevel farTouch = state.getBidAt(0);
//
//            long farTouchPrice = farTouch.price;
//            long farTouchQuantity = farTouch.quantity;
//
//            //profit is equal to the best bid price available - the price that we paid
//            long profit = farTouchPrice - pricePaid;
//
//            //if we can make a profit and we have enough stock to satisfy the order then sell!!
//
//            if (profit > 0 && quantity <= farTouchQuantity) {
//                logger.info("[PASSIVEALGO] Have found a profit I am selling");
//                return new CreateChildOrder(Side.SELL, farTouchQuantity, farTouchPrice);
//
//            } else if (profit < -10 && quantity <= farTouchQuantity) {
//                //-10 is the absolute lowest we are willing to hold our stock at - at this price our position has changed and we want to cut our losses
//                logger.info("[PASSIVEALGO] Preventing further loss, I am selling stock");
//                return new CreateChildOrder(Side.SELL, quantity, farTouchPrice);
//
//            } else {
//                logger.info("[PASSIVEALGO] Potential to make a profit is still present I am holding");
//                return NoAction;
//                //as long as the profit loss is less than -10
//                //we hold our position until the stock goes up which is what we expect according to our analysis
//            }
//        }
//
//        return null;
//
//    }
//
//
//}




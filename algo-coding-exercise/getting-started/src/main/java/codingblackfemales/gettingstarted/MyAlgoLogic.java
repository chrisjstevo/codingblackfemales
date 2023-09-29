package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import static codingblackfemales.action.NoAction.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import codingblackfemales.orderbook.visitor.MutatingMatchOneOrderVisitor;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);
    
    DefaultOrderFlyweight order = new DefaultOrderFlyweight();
    MarketDataOrderFlyweight marketData = new MarketDataOrderFlyweight(Side.SELL, 98, 100);
    

    
    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        
        /********
         *
         * Add your logic here....
         *
         */

        final AskLevel farTouch = state.getAskAt(0);

        long myMaxBid = farTouch.price+10;
        long quantity = 2000;
        long filledQuantity = 0;
        // How to access filledQuantity here?
        // long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        
        // do not place more than 5 orders
        if (state.getChildOrders().size() > 4) {
            return NoAction;
        }

        // place order only when price is within my limit
        if (farTouch.price <= myMaxBid){
            //create an order
            logger.info("[MyALGO] Have:" + state.getChildOrders().size() + " children and "+ filledQuantity +" filledQuantity, joining book with: " + quantity + " @ " + farTouch.price);
            // substract filledQuantity to update quantity
            filledQuantity = marketData.getQuantity();
            quantity -= filledQuantity;
            return new CreateChildOrder(Side.BUY, quantity, farTouch.price);
        } else if (farTouch.price > myMaxBid && state.getChildOrders().size() < 1) { // so it places just 1 order
            //place on the passive side of the book
            logger.info("[MYALGO] Adding order for " + quantity + " @ " + myMaxBid);
            return new CreateChildOrder(Side.BUY, quantity, myMaxBid);
            
        }else{
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, and xx filledQuantity, done.");
            return NoAction;
        }
    }
    
}

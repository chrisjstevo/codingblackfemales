package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//The aim of my algo is to fill my order by creating orders at a more competitive price, quantity than the last order, cancel the order  or no action.

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);
        BidLevel level = state.getBidAt(0);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        //what is the state of the market at the moment? this is getting the active orders without storing it

        var activeOrders = state.getActiveChildOrders();

        // this will show the number of orders in the child orders list. This is getting the last item using the active order size -1
        if ( activeOrders.size() >= 2) {
            var lastOrder = activeOrders.get(activeOrders.size() - 1);
            var secondLastOrder = activeOrders.get(activeOrders.size() - 2);
            // this will compare the price of the active orders and take a % of that and bid more than what was offered
            // try using percentage difference instead of just the difference

            var priceDifference = lastOrder.getPrice() - secondLastOrder.getPrice();

            // I added an if statement to say if the price difference is positive you take an action, if it is negative then no action and the same it is no action

            //The aim is to outbid the last order either by increasing the price or by increasing the quantity of the bid. It will obtain the difference between the last and second last order, if it is greater than 0, it will get the last order quantity and offer the price of the last order+1
            if (priceDifference > 0) {
                var quantity = lastOrder.getQuantity();
                // change 1 so it's not static
                var price = lastOrder.getPrice() + 1;
                return new CreateChildOrder(Side.BUY, quantity, price);

            } else if (priceDifference < 0) {
                // buy more quantity at the same price as the last order
                var quantity = lastOrder.getQuantity() + 20;
                var price = lastOrder.getPrice();
                return new CreateChildOrder(Side.BUY, quantity, price);
            }

        } else if (activeOrders.size() == 1) {
            //var quantity = 10;
            //var price = 80;
            // return new CreateChildOrder(Side.BUY, quantity, price);
            //this scenario will the index of the active order
            var childOrder = activeOrders.get(0);
            return new CancelChildOrder(childOrder);
        }

        return NoAction.NoAction;
    }
}

    }
}

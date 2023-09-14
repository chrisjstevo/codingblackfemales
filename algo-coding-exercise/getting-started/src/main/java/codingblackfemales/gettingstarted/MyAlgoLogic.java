package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AddCancelAlgoLogic;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.event.OrderEventListener;
import codingblackfemales.sequencer.net.TestNetwork;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.PendingOrderDecoder;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);
    private static final Logger cancelLogger = LoggerFactory.getLogger(MyAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[MYALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[MYALGO] Algo Sees Book as:\n" + book);

        final AskLevel farTouch = state.getAskAt(0);

        //take as much as we can from the far touch....
        long quantity = farTouch.quantity;
        long price = farTouch.price;

        //until we have three child orders....
        if (state.getChildOrders().size() < 3) {
            //then keep creating a new one
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 3, sniping far touch of book with: " + quantity + " @ " + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        } else {
            logger.info("[MYALGO] Have:" + state.getChildOrders().size() + " children, want 3, done.");
            return NoAction.NoAction;

        }
    }
    public Action cancelLogicEvaluate(SimpleAlgoState state){
        cancelLogger.info("[ADDCANCELALGO] In Algo Logic....");
        final String book = Util.orderBookToString(state);
        cancelLogger.info("[ADDCANCELALGO] Algo Sees Book as:\n" + book);

        var totalOrderCount = state.getChildOrders().size();

        //make sure we have an exit condition...
        if (totalOrderCount > 20) {
            return NoAction.NoAction;
        }

        final var activeOrders = state.getActiveChildOrders();

        if (activeOrders.size() > 0) {

            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var childOrder = option.get();
                cancelLogger.info("[ADDCANCELALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            }
            else{
                return NoAction.NoAction;
            }
        } else {
            BidLevel level = state.getBidAt(0);
            final long priceLevel = level.price;
            final long quantityLevel = level.quantity;
            logger.info("[ADDCANCELALGO] Adding order for" + quantityLevel + "@" + priceLevel);
            return new CreateChildOrder(Side.BUY, quantityLevel, priceLevel);
        }

    }
}

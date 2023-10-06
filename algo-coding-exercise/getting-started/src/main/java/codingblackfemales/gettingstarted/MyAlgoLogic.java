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

    @Override
    public Action evaluate(SimpleAlgoState state) {
        logger.info("[MYALGO] In Algo Logic....");

        final String book = Util.orderBookToString(state);

        logger.info("[MYALGO] Algo Sees Book as:\n" + book);

        var totalOrderCount = state.getChildOrders().size();
        BidLevel bidLevel = state.getBidAt(0);
        AskLevel askLevel = state.getAskAt(0);

        final long buyPrice = bidLevel.price;
        final long sellPrice = askLevel.price;

        final long buyQuantity = bidLevel.quantity;
        final long sellQuantity = askLevel.quantity;

        // Make sure we have an exit condition...
        if (totalOrderCount > 20) {
            return NoAction.NoAction.NoAction;
        }

        final var activeOrders = state.getActiveChildOrders();

        if (activeOrders.size() > 0) {
            final var option = activeOrders.stream().findFirst();

            if(totalOrderCount < 4)  {
                logger.info("MYPassiveALGO Adding order for " + buyQuantity + "@" + buyPrice);
                CreateChildOrder createOrder = new CreateChildOrder(Side.BUY, buyQuantity, buyPrice);
                logger.info("[MYALGO] Child Order Details: " + createOrder);
                logger.info("Order count as:\n" + totalOrderCount);

                return createOrder;


            } else if(sellPrice < buyPrice){
                var childOrder = option.get();
                logger.info("[MYALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);

            }

        }
            return NoAction.NoAction.NoAction; // Do nothing if askLevel price is higher than bidLevel price

        }
    }


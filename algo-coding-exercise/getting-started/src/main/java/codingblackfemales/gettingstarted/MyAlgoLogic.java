package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.SimpleAlgoStateImpl;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MyAlgoLogic implements AlgoLogic {


    RunTrigger runTrigger = new RunTrigger();
    MarketDataService dataService = new MarketDataService(runTrigger);
    OrderService orderService = new OrderService(runTrigger);
    SimpleAlgoState state = new SimpleAlgoStateImpl(dataService,orderService);
    public long price = 0;
    public long quantity = 0;


    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);



    public long askVwapCalculator() {
        long totalValue = 0;
        long totalVolume = 0;
       int askLevels = state.getAskLevels();

        for (int i = 0; i < askLevels; i++) {
            AskLevel askLevel = state.getAskAt(i);
            totalValue += askLevel.getPrice() * askLevel.getQuantity();
            totalVolume += askLevel.getQuantity();

            System.out.println("Ask Level " + i + ": Price = " + askLevel.getPrice() + ", Quantity = " + askLevel.getQuantity());

        }
        if (totalVolume == 0) {
            return 0;
        }

        return totalValue/totalVolume;
    }

    public long bidVwapCalculator() {
        long totalValue = 0;
        long totalVolume = 0;
        int bidLevels = state.getBidLevels();

        for (int i = 0; i < bidLevels; i++) {
            BidLevel bidLevel = state.getBidAt(i);
            totalValue += bidLevel.getPrice() * bidLevel.getQuantity();
            totalVolume += bidLevel.getQuantity();

            System.out.println("Ask Level " + i + ": Price = " + bidLevel.getPrice() + ", Quantity = " + bidLevel.getQuantity());

        }
        if (totalVolume == 0) {
            return 0;
        }

        return totalValue/totalVolume;
    }

        @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        if ( price > (1.05 * bidVwapCalculator())){
            return new NoAction();
        }
        else if (price < (1.05 * bidVwapCalculator())){
            return new CreateChildOrder(Side.BUY,quantity, price);
        }


        if(price < (1.05 * askVwapCalculator())) {
            return new NoAction();
        } else if (price > (1.05 * askVwapCalculator())){
            return new CreateChildOrder(Side.SELL,quantity,price);
        }
            return new NoAction();

        }

}

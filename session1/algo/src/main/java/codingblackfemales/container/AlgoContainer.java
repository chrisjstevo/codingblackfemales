package codingblackfemales.container;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sequencer.net.Consumer;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.SimpleAlgoStateImpl;
import org.agrona.DirectBuffer;

public class AlgoContainer implements Consumer {

    private final MarketDataService marketDataService;
    private final OrderService orderService;

    private final RunTrigger runTrigger;

    private AlgoLogic logic;

    private final SimpleAlgoState state;

    public AlgoContainer(final MarketDataService marketDataService, final OrderService orderService, final RunTrigger runTrigger) {
        this.marketDataService = marketDataService;
        this.orderService = orderService;
        this.runTrigger = runTrigger;
        this.state = new SimpleAlgoStateImpl(marketDataService, orderService);
    }

    public MarketDataService getMarketDataService(){
        return marketDataService;
    }

    public OrderService getOrderService(){
        return orderService;
    }

    public void setLogic(AlgoLogic logic){
        this.logic = logic;
    }

    @Override
    public void onMessage(DirectBuffer buffer) throws Exception {
        if(runTrigger.shouldRun()){
            runAlgoLogic();
        }else {
            //do nothing...
        }
    }

    private void runAlgoLogic(){
        logic.evaluate(state);
    }
}

package codingblackfemales.gettingstarted;


import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AddCancelAlgoLogic;
import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.SimpleAlgoStateImpl;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

class MyAlgoLogicTest {

    MyAlgoLogic algoLogic = new MyAlgoLogic();
    RunTrigger runTrigger;
    MarketDataService dataService;
    OrderService orderService;
    Actioner actioner;
    AlgoContainer container;
    SimpleAlgoState state;


    @org.junit.jupiter.api.Test
    void createTick2() {

    }

    @org.junit.jupiter.api.Test
    void evaluate() {

        container = new AlgoContainer(new MarketDataService(runTrigger), new OrderService(runTrigger), runTrigger, actioner);
        //set my algo logic
        container.setLogic(new MyAlgoLogic());

        this.state = new SimpleAlgoStateImpl(dataService, orderService);

        this.algoLogic.evaluate(state);

        var result   = NoAction.NoAction;

        assertEquals(result,state);


    }
}
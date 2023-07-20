package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.OrderSide;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;

public class ExampleAlgoLogic implements AlgoLogic{

    @Override
    public Action evaluate(SimpleAlgoState state) {

        System.out.println("In Algo Logic....");

        final AskLevel farTouch = state.getAskAt(0);

        long quantity = farTouch.quantity / 2;
        long price = farTouch.price;
        long instruentId = state.getInstrumentId();

        return new CreateChildOrder(instruentId, OrderSide.BUY, quantity, price);
    }
}

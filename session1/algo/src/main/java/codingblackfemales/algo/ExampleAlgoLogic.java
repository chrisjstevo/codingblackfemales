package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import messages.order.Side;

import static codingblackfemales.action.NoAction.NoAction;

public class ExampleAlgoLogic implements AlgoLogic{

    @Override
    public Action evaluate(SimpleAlgoState state) {

        System.out.println("In Algo Logic....");

        final AskLevel farTouch = state.getAskAt(0);

        long quantity = farTouch.quantity / 2;
        long price = farTouch.price;

        //until we have three child orders....
        if(state.getChildOrders().size() < 3){
            //then keep creating a new one
            System.out.println("Have:" + state.getChildOrders().size() + " children, want 3, carrying on...");
            return new CreateChildOrder(Side.BUY, quantity, price);
        }else{
            System.out.println("Have:" + state.getChildOrders().size() + " children, want 3, all good.");
            return NoAction;
        }

    }
}

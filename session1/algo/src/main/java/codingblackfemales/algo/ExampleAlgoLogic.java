package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.sotw.SimpleAlgoState;

public class ExampleAlgoLogic implements AlgoLogic{

    @Override
    public Action evaluate(SimpleAlgoState state) {

        System.out.println("In Algo Logic....");

        final int bidLevels = state.getBidLevels();
        final int askLevels = state.getAskLevels();

        for(int i=0; i<bidLevels; i++){
            System.out.println("i = " + i + " " + state.getBidAt(i));
        }

        for(int i=0; i<askLevels; i++){
            System.out.println("i = " + i + " " + state.getAskAt(i));
        }

        return null;
    }
}

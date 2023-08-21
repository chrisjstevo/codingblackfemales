package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StretchAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(StretchAlgoLogic.class);
    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[STRETCHALGO] In Algo Logic....");

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[STRETCHALGO] The state of the order book is:\n" + orderBookAsString);



        return NoAction.NoAction;
    }
}

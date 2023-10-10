package codingblackfemales.container;

import codingblackfemales.action.Action;
import codingblackfemales.sequencer.Sequencer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Actioner {
    private static final Logger logger = LoggerFactory.getLogger(Actioner.class);

    private final Sequencer sequencer;

    public Actioner(Sequencer sequencer) {
        this.sequencer = sequencer;
    }

    public void processAction(final Action action){
        logger.info("[ALGO] Actioner, sending action:" + action);
        action.apply(sequencer);
    }
}

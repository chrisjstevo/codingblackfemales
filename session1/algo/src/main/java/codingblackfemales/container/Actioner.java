package codingblackfemales.container;

import codingblackfemales.action.Action;
import codingblackfemales.sequencer.Sequencer;

public class Actioner {

    private final Sequencer sequencer;

    public Actioner(Sequencer sequencer) {
        this.sequencer = sequencer;
    }

    public void processAction(final Action action){
        action.apply(sequencer);
    }
}

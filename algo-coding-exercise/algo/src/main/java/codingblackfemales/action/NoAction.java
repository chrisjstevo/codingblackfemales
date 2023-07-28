package codingblackfemales.action;

import codingblackfemales.sequencer.Sequencer;

public class NoAction implements Action {

    public static final NoAction NoAction = new NoAction();

    @Override
    public String toString() {
        return "NoAction()";
    }

    @Override
    public void apply(Sequencer sequencer) {
    }
}

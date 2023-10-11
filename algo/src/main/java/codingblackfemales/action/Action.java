package codingblackfemales.action;

import codingblackfemales.sequencer.Sequencer;

public interface Action {

    void apply(final Sequencer sequencer);

}

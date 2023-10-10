package codingblackfemales.sequencer.marketdata;

import codingblackfemales.sequencer.Sequencer;
import org.agrona.DirectBuffer;

public abstract class SequencerTestCase {

    public abstract Sequencer getSequencer();

    private final Sequencer sequencer = getSequencer();

    public Sequencer getSequencerInternal(){
        return sequencer;
    }

    public void send(DirectBuffer buffer) throws Exception{
        getSequencerInternal().onCommand(buffer);
    }

}

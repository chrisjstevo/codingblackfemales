package codingblackfemales.sequencer.marketdata;

import codingblackfemales.sequencer.Sequencer;
import org.agrona.DirectBuffer;

public abstract class SequencerTestCase {

    public abstract Sequencer getSequencer();

    public void send(DirectBuffer buffer) throws Exception{
        getSequencer().onCommand(buffer);
    }

}

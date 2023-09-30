package codingblackfemales.sequencer.marketdata;

import codingblackfemales.sequencer.Sequencer;
import org.agrona.DirectBuffer;

public abstract class SequencerTestCase {

    private final Sequencer sequencer = getSequencer();

    public abstract Sequencer getSequencer();

    public Sequencer getSequencerInternal() {
        return sequencer;
    }

    public void send(DirectBuffer buffer) throws Exception {
        getSequencerInternal().onCommand(buffer);
    }

    public void customSend(DirectBuffer[] buffers) throws Exception {
        for (DirectBuffer buffer : buffers) {
            getSequencerInternal().onCommand(buffer);
        }
    }


}

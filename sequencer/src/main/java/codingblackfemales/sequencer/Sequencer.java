package codingblackfemales.sequencer;

import org.agrona.DirectBuffer;

public interface Sequencer {
    public void onCommand(final DirectBuffer byteBuffer);
}

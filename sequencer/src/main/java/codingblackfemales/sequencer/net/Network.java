package codingblackfemales.sequencer.net;

import org.agrona.DirectBuffer;

public interface Network {
    public void dispatch(final DirectBuffer buffer);
}

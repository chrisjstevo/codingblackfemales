package codingblackfemales.sequencer.net;

import org.agrona.DirectBuffer;

public interface Consumer {
    public void onMessage(final DirectBuffer buffer);
}

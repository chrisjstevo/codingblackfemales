package codingblackfemales.orderbook.channel;

import codingblackfemales.sequencer.Sequencer;
import org.agrona.DirectBuffer;

public class MarketDataChannel {

    private final Sequencer sequencer;

    public MarketDataChannel(Sequencer sequencer) {
        this.sequencer = sequencer;
    }

    public void publish(DirectBuffer buffer) throws Exception{
        sequencer.onCommand(buffer);
    }
}

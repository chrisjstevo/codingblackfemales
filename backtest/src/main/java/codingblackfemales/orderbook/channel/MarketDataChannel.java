package codingblackfemales.orderbook.channel;

import codingblackfemales.sequencer.Sequencer;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketDataChannel {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataChannel.class);

    private final Sequencer sequencer;

    public MarketDataChannel(Sequencer sequencer) {
        this.sequencer = sequencer;
    }

    public void publish(DirectBuffer buffer){
        logger.info("[ORDERBOOK] Sending market data update...");
        sequencer.onCommand(buffer);
    }
}

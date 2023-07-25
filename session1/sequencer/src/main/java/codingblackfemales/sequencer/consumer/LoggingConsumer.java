package codingblackfemales.sequencer.consumer;

import codingblackfemales.sequencer.net.Consumer;
import messages.marketdata.BookUpdateDecoder;
import messages.marketdata.MessageHeaderDecoder;
import messages.order.CreateOrderDecoder;
import messages.order.CreateOrderEncoder;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingConsumer implements Consumer {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConsumer.class);

    private final MessageHeaderDecoder decoder = new MessageHeaderDecoder();
    private final BookUpdateDecoder bookUpdateDecoder = new BookUpdateDecoder();
    private final CreateOrderDecoder createOrderDecoder = new CreateOrderDecoder();

    @Override
    public void onMessage(final DirectBuffer buffer) {

        decoder.wrap(buffer, 0);

        if(decoder.schemaId() == BookUpdateDecoder.SCHEMA_ID){
            final int actingBlockLength = decoder.blockLength();
            final int actingVersion = decoder.version();
            int bufferOffset = decoder.encodedLength();
            bookUpdateDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            logger.info("[" + decoder.sequencerNumber() + "] " + bookUpdateDecoder);
        }else if(decoder.schemaId() == CreateOrderEncoder.SCHEMA_ID){
            final int actingBlockLength = decoder.blockLength();
            final int actingVersion = decoder.version();

            int bufferOffset = decoder.encodedLength();
            createOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            logger.info("[" + decoder.sequencerNumber() + "] " + createOrderDecoder);
        }

    }
}

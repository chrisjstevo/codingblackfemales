package codingblackfemales.sequencer.consumer;

import codingblackfemales.sequencer.net.Consumer;
import messages.marketdata.BookUpdateDecoder;
import messages.marketdata.MessageHeaderDecoder;
import messages.order.CancelOrderDecoder;
import messages.order.CreateOrderDecoder;
import messages.order.CreateOrderEncoder;
import messages.order.FillOrderDecoder;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.sequencer.util.MessageUtil.bookUpdateToString;

public class LoggingConsumer implements Consumer {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConsumer.class);

    private final MessageHeaderDecoder decoder = new MessageHeaderDecoder();
    private final BookUpdateDecoder bookUpdateDecoder = new BookUpdateDecoder();
    private final CreateOrderDecoder createOrderDecoder = new CreateOrderDecoder();
    private final FillOrderDecoder fillDecoder = new FillOrderDecoder();

    private final CancelOrderDecoder cancelDecoder = new CancelOrderDecoder();

    @Override
    public void onMessage(final DirectBuffer buffer) {

        decoder.wrap(buffer, 0);

        if (decoder.schemaId() == BookUpdateDecoder.SCHEMA_ID && decoder.templateId() == BookUpdateDecoder.TEMPLATE_ID) {
            final int actingBlockLength = decoder.blockLength();
            final int actingVersion = decoder.version();
            int bufferOffset = decoder.encodedLength();
            bookUpdateDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            logger.info("[" + decoder.sequencerNumber() + "] \n" + bookUpdateToString(bookUpdateDecoder));
        } else if (decoder.schemaId() == CreateOrderEncoder.SCHEMA_ID && decoder.templateId() == CreateOrderDecoder.TEMPLATE_ID) {
            final int actingBlockLength = decoder.blockLength();
            final int actingVersion = decoder.version();

            int bufferOffset = decoder.encodedLength();
            createOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            logger.info("[" + decoder.sequencerNumber() + "] " + createOrderDecoder);
        } else if (decoder.schemaId() == FillOrderDecoder.SCHEMA_ID && decoder.templateId() == FillOrderDecoder.TEMPLATE_ID) {
            final int actingBlockLength = decoder.blockLength();
            final int actingVersion = decoder.version();
            int bufferOffset = decoder.encodedLength();
            fillDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            logger.info("[" + decoder.sequencerNumber() + "] " + fillDecoder);
        }else if (decoder.schemaId() == CancelOrderDecoder.SCHEMA_ID && decoder.templateId() == CancelOrderDecoder.TEMPLATE_ID) {
            final int actingBlockLength = decoder.blockLength();
            final int actingVersion = decoder.version();
            int bufferOffset = decoder.encodedLength();
            cancelDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            logger.info("[" + decoder.sequencerNumber() + "] " + cancelDecoder);
        }
    }
}

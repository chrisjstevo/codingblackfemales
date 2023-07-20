package codingblackfemales.sequencer.consumer;

import codingblackfemales.sequencer.net.Consumer;
import messages.marketdata.BookUpdateDecoder;
import messages.marketdata.MessageHeaderDecoder;
import messages.order.CreateOrderDecoder;
import messages.order.CreateOrderEncoder;
import org.agrona.DirectBuffer;

public class LoggingConsumer implements Consumer {

    private final MessageHeaderDecoder decoder = new MessageHeaderDecoder();
    private final BookUpdateDecoder bookUpdateDecoder = new BookUpdateDecoder();
    private final CreateOrderDecoder createOrderDecoder = new CreateOrderDecoder();

    @Override
    public void onMessage(final DirectBuffer buffer) throws Exception {

        decoder.wrap(buffer, 0);

        if(decoder.schemaId() == BookUpdateDecoder.SCHEMA_ID){
            final int actingBlockLength = decoder.blockLength();
            final int actingVersion = decoder.version();

            int bufferOffset = decoder.encodedLength();
            bookUpdateDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            System.out.println("[" + decoder.sequencerNumber() + "] " + bookUpdateDecoder);
        }else if(decoder.schemaId() == CreateOrderEncoder.SCHEMA_ID){
            final int actingBlockLength = decoder.blockLength();
            final int actingVersion = decoder.version();

            int bufferOffset = decoder.encodedLength();
            createOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            System.out.println("[" + decoder.sequencerNumber() + "] " + createOrderDecoder);
        }

    }
}

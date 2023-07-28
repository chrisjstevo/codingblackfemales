package codingblackfemales.sequencer.event;

import codingblackfemales.sequencer.net.Consumer;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BidBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;
import messages.marketdata.MessageHeaderDecoder;
import org.agrona.DirectBuffer;

public abstract class MarketDataEventListener implements Consumer {

    private final MessageHeaderDecoder header = new MessageHeaderDecoder();
    private final BookUpdateDecoder book = new BookUpdateDecoder();
    private final AskBookUpdateDecoder ask = new AskBookUpdateDecoder();
    private final BidBookUpdateDecoder bid = new BidBookUpdateDecoder();

    @Override
    public void onMessage(final DirectBuffer buffer) {
        header.wrap(buffer, 0);

        final int actingBlockLength = header.blockLength();
        final int actingVersion = header.version();
        final int bufferOffset = header.encodedLength();

        if(header.schemaId() == BookUpdateDecoder.SCHEMA_ID){
            book.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            onBookUpdate(book);
        }else if(header.schemaId() == AskBookUpdateDecoder.SCHEMA_ID){
            ask.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            onAskBook(ask);
        }else if(header.schemaId() == BidBookUpdateDecoder.SCHEMA_ID){
            bid.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
            onBidBook(bid);
        }
    }

    public abstract void onBookUpdate(BookUpdateDecoder bookUpdate);
    public abstract void onAskBook(AskBookUpdateDecoder askBook);
    public abstract void onBidBook(BidBookUpdateDecoder bidBook);

}

package codingblackfemales.marketdata.api;

import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

import static java.lang.String.format;

public class MarketDataEncoder {
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder bookUpdateEncoder = new BookUpdateEncoder();
    private final AskBookUpdateEncoder askBookUpdateEncoder = new AskBookUpdateEncoder();
    private final BidBookUpdateEncoder bidBookUpdateEncoder = new BidBookUpdateEncoder();

    public UnsafeBuffer encode(final MarketDataMessage message) {
        switch (message.updateType()) {
            case BookUpdate:
                return doEncode((BookUpdate) message);
            case AskUpdate:
                return doEncode((AskBookUpdate) message);
            case BidUpdate:
                return doEncode((BidBookUpdate) message);
            default:
                throw new RuntimeException(format("Unsupported updateType=[%s] message=[%s]", message.updateType(), message));
        }
    }

    private UnsafeBuffer doEncode(final AskBookUpdate update) {
        final UnsafeBuffer directBuffer = buffer();
        askBookUpdateEncoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        askBookUpdateEncoder.venue(update.venue());
        askBookUpdateEncoder.instrumentId(update.instrumentId());
        AskBookUpdateEncoder.AskBookEncoder askBookEncoder = askBookUpdateEncoder.askBookCount(update.askBook().size());
        for (int i = 0; i < update.askBook().size(); i++) {
            BookEntry bookEntry = update.askBook().get(i);
            askBookEncoder.next().price(bookEntry.price()).size(bookEntry.size());
        }
        return directBuffer;
    }

    private UnsafeBuffer doEncode(final BidBookUpdate update) {
        final UnsafeBuffer directBuffer = buffer();
        bidBookUpdateEncoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        bidBookUpdateEncoder.venue(update.venue());
        bidBookUpdateEncoder.instrumentId(update.instrumentId());
        BidBookUpdateEncoder.BidBookEncoder bidBookEncoder = bidBookUpdateEncoder.bidBookCount(update.bidBook().size());
        for (int i = 0; i < update.bidBook().size(); i++) {
            BookEntry bookEntry = update.bidBook().get(i);
            bidBookEncoder.next().price(bookEntry.price()).size(bookEntry.size());
        }
        return directBuffer;
    }

    private UnsafeBuffer doEncode(final BookUpdate update) {
        final UnsafeBuffer directBuffer = buffer();
        bookUpdateEncoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        bookUpdateEncoder.venue(update.venue());
        bookUpdateEncoder.instrumentId(update.instrumentId());

        BookUpdateEncoder.AskBookEncoder askBookEncoder = bookUpdateEncoder.askBookCount(update.askBook().size());
        for (int i = 0; i < update.askBook().size(); i++) {
            BookEntry bookEntry = update.askBook().get(i);
            askBookEncoder.next().price(bookEntry.price()).size(bookEntry.size());
        }

        BookUpdateEncoder.BidBookEncoder bidBookEncoder = bookUpdateEncoder.bidBookCount(update.bidBook().size());
        for (int i = 0; i < update.bidBook().size(); i++) {
            BookEntry bookEntry = update.bidBook().get(i);
            bidBookEncoder.next().price(bookEntry.price()).size(bookEntry.size());
        }

        bookUpdateEncoder.instrumentStatus(update.instrumentStatus());
        bookUpdateEncoder.source(Source.STREAM);
        return directBuffer;
    }

    private UnsafeBuffer buffer() {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        return new UnsafeBuffer(byteBuffer);
    }
}

package codingblackfemales.marketdata.impl;

import codingblackfemales.marketdata.api.AskBookUpdate;
import codingblackfemales.marketdata.api.BookEntry;
import messages.marketdata.Venue;

import java.util.List;

public class AskBookUpdateImpl implements AskBookUpdate {
    private long instrumentId;
    private Venue venue;
    private List<BookEntry> askBook;

    public AskBookUpdateImpl(long instrumentId, Venue venue, List<BookEntry> askBook) {
        this.instrumentId = instrumentId;
        this.venue = venue;
        this.askBook = askBook;
    }

    @Override
    public List<BookEntry> askBook() {
        return askBook;
    }

    @Override
    public long instrumentId() {
        return instrumentId;
    }

    @Override
    public Venue venue() {
        return venue;
    }

    @Override
    public String toString() {
        return "AskBookUpdateImpl{" +
                "instrumentId=" + instrumentId +
                ", venue=" + venue +
                ", askBook=" + askBook +
                '}';
    }
}

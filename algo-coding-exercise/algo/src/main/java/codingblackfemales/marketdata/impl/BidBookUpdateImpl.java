package codingblackfemales.marketdata.impl;

import codingblackfemales.marketdata.api.BidBookUpdate;
import codingblackfemales.marketdata.api.BookEntry;
import messages.marketdata.Venue;

import java.util.List;

public class BidBookUpdateImpl implements BidBookUpdate {
    private long instrumentId;
    private Venue venue;
    private List<BookEntry> bidBook;

    public BidBookUpdateImpl(long instrumentId, Venue venue, List<BookEntry> bidBook) {
        this.instrumentId = instrumentId;
        this.venue = venue;
        this.bidBook = bidBook;
    }

    @Override
    public List<BookEntry> bidBook() {
        return bidBook;
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
        return "BidBookUpdateImpl{" +
                "instrumentId=" + instrumentId +
                ", venue=" + venue +
                ", bidBook=" + bidBook +
                '}';
    }
}

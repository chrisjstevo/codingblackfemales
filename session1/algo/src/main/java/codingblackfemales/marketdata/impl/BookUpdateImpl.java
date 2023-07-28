package codingblackfemales.marketdata.impl;

import codingblackfemales.marketdata.api.BookEntry;
import codingblackfemales.marketdata.api.BookUpdate;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.Venue;

import java.util.List;

public class BookUpdateImpl implements BookUpdate {
    private long instrumentId;
    private Venue venue;
    private InstrumentStatus instrumentStatus;
    private List<BookEntry> bidBook;
    private List<BookEntry> askBook;

    public BookUpdateImpl(long instrumentId, Venue venue, InstrumentStatus instrumentStatus, List<BookEntry> bidBook, List<BookEntry> askBook) {
        this.instrumentId = instrumentId;
        this.venue = venue;
        this.instrumentStatus = instrumentStatus;
        this.bidBook = bidBook;
        this.askBook = askBook;
    }

    @Override
    public InstrumentStatus instrumentStatus() {
        return instrumentStatus;
    }

    @Override
    public List<BookEntry> bidBook() {
        return bidBook;
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
        return "BookUpdateImpl{" +
                "instrumentId=" + instrumentId +
                ", venue=" + venue +
                ", instrumentStatus=" + instrumentStatus +
                ", bidBook=" + bidBook +
                ", askBook=" + askBook +
                '}';
    }
}

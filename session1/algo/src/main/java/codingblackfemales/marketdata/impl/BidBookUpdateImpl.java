package codingblackfemales.marketdata.impl;

import codingblackfemales.marketdata.api.BidBookUpdate;
import codingblackfemales.marketdata.api.BookEntry;
import messages.marketdata.Venue;

import java.util.List;

public class BidBookUpdateImpl implements BidBookUpdate {
    @Override
    public List<BookEntry> bidBook() {
        return null;
    }

    @Override
    public long instrumentId() {
        return 0;
    }

    @Override
    public Venue venue() {
        return null;
    }
}

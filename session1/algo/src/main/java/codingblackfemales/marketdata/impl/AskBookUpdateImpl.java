package codingblackfemales.marketdata.impl;

import codingblackfemales.marketdata.api.AskBookUpdate;
import codingblackfemales.marketdata.api.BookEntry;
import messages.marketdata.Venue;

import java.util.List;

public class AskBookUpdateImpl implements AskBookUpdate {
    @Override
    public List<BookEntry> askBook() {
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

package codingblackfemales.marketdata.gen;

import codingblackfemales.marketdata.api.BookEntry;
import codingblackfemales.marketdata.api.BookUpdate;
import codingblackfemales.marketdata.api.MarketDataMessage;
import codingblackfemales.marketdata.impl.BookUpdateImpl;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.Venue;

import java.util.ArrayList;
import java.util.List;

public class RandomMarketDataGenerator implements MarketDataGenerator {

    @Override
    public MarketDataMessage next() {
        return nextBookUpdate();
    }


    private BookUpdate nextBookUpdate() {
        List<BookEntry> bidBook = new ArrayList<>();
        bidBook.add(new BookEntry().setPrice(100).setSize(20));
        List<BookEntry> askBook = new ArrayList<>();
        askBook.add(new BookEntry().setPrice(110).setSize(30));
        return new BookUpdateImpl(1, Venue.XLON, InstrumentStatus.CONTINUOUS, bidBook, askBook);
    }
}

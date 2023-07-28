package codingblackfemales.marketdata.api;

import messages.marketdata.InstrumentStatus;

import java.util.List;

public interface BookUpdate extends MarketDataMessage {

    default UpdateType updateType() {
        return UpdateType.BookUpdate;
    }

    InstrumentStatus instrumentStatus();

    List<BookEntry> bidBook();

    List<BookEntry> askBook();
}

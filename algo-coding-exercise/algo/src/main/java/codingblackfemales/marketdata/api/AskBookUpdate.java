package codingblackfemales.marketdata.api;

import java.util.List;

public interface AskBookUpdate extends MarketDataMessage {

    default UpdateType updateType() {
        return UpdateType.AskUpdate;
    }

    List<BookEntry> askBook();
}

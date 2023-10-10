package codingblackfemales.marketdata.api;

import java.util.List;

public interface BidBookUpdate extends MarketDataMessage {

    default UpdateType updateType() {
        return UpdateType.BidUpdate;
    }

    List<BookEntry> bidBook();
}

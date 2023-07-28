package codingblackfemales.marketdata.api;

import messages.marketdata.Venue;

public interface MarketDataMessage {
    long instrumentId();

    Venue venue();

    UpdateType updateType();
}

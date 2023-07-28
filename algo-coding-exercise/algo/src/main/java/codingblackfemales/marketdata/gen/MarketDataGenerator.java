package codingblackfemales.marketdata.gen;

import codingblackfemales.marketdata.api.MarketDataMessage;

public interface MarketDataGenerator {
    MarketDataMessage next();
}

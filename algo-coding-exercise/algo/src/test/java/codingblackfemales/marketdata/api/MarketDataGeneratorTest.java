package codingblackfemales.marketdata.api;

import codingblackfemales.marketdata.gen.RandomMarketDataGenerator;
import codingblackfemales.marketdata.gen.SimpleFileMarketDataGenerator;
import messages.marketdata.Venue;
import org.junit.Before;
import org.junit.Test;

public class MarketDataGeneratorTest {
    private SimpleFileMarketDataGenerator marketDataGenerator;

    @Before
    public void setup() {
        final long instrumentId = 1234;
        final Venue venue = Venue.XLON;
        final long priceLevel = 1000;
        final long priceMaxDelta = 100;
        marketDataGenerator = new SimpleFileMarketDataGenerator("src/test/resources/marketdata.json", new RandomMarketDataGenerator(instrumentId, venue, priceLevel, priceMaxDelta, 15));
    }

    @Test
    public void should_generate(){
        marketDataGenerator.generate(1000);
        marketDataGenerator.close();
    }
}

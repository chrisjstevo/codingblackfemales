package codingblackfemales.marketdata.api;


import codingblackfemales.container.RunTrigger;
import codingblackfemales.marketdata.impl.SimpleFileMarketDataProvider;
import codingblackfemales.service.MarketDataService;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Before;
import org.junit.Test;

public class MarketDataProviderTest {
    private MarketDataProvider provider;
    private MarketDataEncoder encoder;
    private MarketDataService marketDataService;

    @Before
    public void setup() {
        provider = new SimpleFileMarketDataProvider("src/test/resources/marketdata.json");
        encoder = new MarketDataEncoder();
        marketDataService = new MarketDataService(new RunTrigger());
    }

    @Test
    public void should_process_market_data() {
        MarketDataMessage marketDataMessage;
        while((marketDataMessage = provider.poll())!=null) {
            UnsafeBuffer encoded = encoder.encode(marketDataMessage);
            marketDataService.onMessage(encoded);
        }
    }
}
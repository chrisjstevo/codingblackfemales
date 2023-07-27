package codingblackfemales.marketdata.api;

import codingblackfemales.marketdata.gen.RandomMarketDataGenerator;
import codingblackfemales.marketdata.gen.SimpleFileMarketDataGenerator;
import org.junit.Before;
import org.junit.Test;

public class MarketDataGeneratorTest {
    private SimpleFileMarketDataGenerator marketDataGenerator;

    @Before
    public void setup() {
        marketDataGenerator = new SimpleFileMarketDataGenerator("src/test/resources/marketdata.json", new RandomMarketDataGenerator());
    }

    @Test
    public void should_generate(){
        marketDataGenerator.generate(100);
        marketDataGenerator.close();
    }
}

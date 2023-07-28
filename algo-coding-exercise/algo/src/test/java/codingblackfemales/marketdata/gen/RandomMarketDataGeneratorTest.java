package codingblackfemales.marketdata.gen;

import codingblackfemales.marketdata.api.MarketDataMessage;
import messages.marketdata.Venue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomMarketDataGeneratorTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void should_generate_marketdata() {
        final RandomMarketDataGenerator generator = new RandomMarketDataGenerator(1, Venue.XLON, 1_000, 100, 15);
        logger.info("{}",generator);
        for(int i = 0; i < 1_000; i++){
            MarketDataMessage marketDataMessage = generator.updateBook();
            logger.info("{}",generator);
            logger.info("{}",marketDataMessage);
        }
    }
}
package codingblackfemales.marketdata.gen;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomMarketDataGeneratorTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void should_generate_marketdata() {
        final RandomMarketDataGenerator generator = new RandomMarketDataGenerator();
        generator.initBook(1_000, 100);
        System.out.println(String.format("%s",generator));
        for(int i = 0; i < 100; i++){
            generator.updateBook();
            logger.info("{}",generator);
        }
    }
}
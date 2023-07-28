package codingblackfemales.marketdata.gen;

import codingblackfemales.marketdata.api.MarketDataMessage;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleFileMarketDataGenerator {
    private final Gson gson = new Gson();
    private final String marketDataFileName;
    private final File marketDataFile;
    private final BufferedWriter writer;
    private final String lineSeparator;
    private final MarketDataGenerator generator;

    public SimpleFileMarketDataGenerator(final String marketDataFileName,
                                         final MarketDataGenerator generator) {
        this.generator = generator;
        try {
            this.marketDataFileName = marketDataFileName;
            marketDataFile = new File(marketDataFileName);
            if (marketDataFile.exists()) {
                marketDataFile.delete();
            }
            lineSeparator = System.lineSeparator();
            writer = new BufferedWriter(new FileWriter(marketDataFile));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to create market data file marketDataFileName=[%s]", marketDataFileName), e);
        }
    }

    public void close() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to create market data file marketDataFileName=[%s]", marketDataFileName), e);
        }
    }

    public void generate(final int entriesCount) {
        try {
            for (int i = 0; i < entriesCount; i++) {
                MarketDataMessage marketDataMessage = generator.next();
                writer.write(marketDataMessage.updateType().ordinal() + "," + gson.toJson(marketDataMessage) + lineSeparator);
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to create market data file marketDataFileName=[%s]", marketDataFileName), e);
        }
    }
}

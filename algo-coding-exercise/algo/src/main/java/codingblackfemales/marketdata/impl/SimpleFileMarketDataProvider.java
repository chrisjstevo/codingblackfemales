package codingblackfemales.marketdata.impl;

import codingblackfemales.marketdata.api.MarketDataMessage;
import codingblackfemales.marketdata.api.MarketDataProvider;
import codingblackfemales.marketdata.api.UpdateType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.Validate;

import java.io.*;

public class SimpleFileMarketDataProvider implements MarketDataProvider {
    private final Gson gson;
    private final File marketDataFile;
    private final FileReader reader;
    private final BufferedReader bufferedReader;
    private final String marketDataFileName;

    public SimpleFileMarketDataProvider(final String marketDataFileName) {
        this.marketDataFileName = marketDataFileName;
        try {
            marketDataFile = new File(marketDataFileName);
            Validate.isTrue(marketDataFile.canRead(), "Unable to read marketDataFileName=[%s] marketDataFile.path=[%s]", marketDataFileName, marketDataFile.getAbsolutePath());
            reader = new FileReader(marketDataFile);
            bufferedReader = new BufferedReader(reader);

            gson = new GsonBuilder().create();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("Failed to read file marketDataFileName=[%s]", marketDataFileName), e);
        }
    }

    @Override
    public MarketDataMessage poll() {
        return nextMessage();
    }

    private MarketDataMessage nextMessage() {
        try {
            final String line = bufferedReader.readLine();
            if(line ==null){
                return null;
            }
            String substring = line.substring(0, 1);
            UpdateType updateType = UpdateType.valueOf(Integer.parseInt(substring));
            return gson.fromJson(line.substring(2, line.length()), updateType.getMessageClass());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to read file marketDataFileName=[%s]", marketDataFileName), e);
        }
    }
}

package codingblackfemales.moneymakingalgo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
This algo trades FX and uses Simple Moving average as an indicator
 */
public class AlgoMakingMoney {
    private static final String apiEndpoint = System.getenv("API_ENDPOINT");
    private static String apiKey = System.getenv("API_KEY");
    private static double balance, positionSize;
    private static int numberOfDays, indicatorPeriod;//Period for the SMA indicator

    public static void main(String[] args) throws IOException {

        double[] exchangeRates = new double[numberOfDays];

        for (int day = 1; day <= numberOfDays; day++) {
            // In a live trading scenario, replace this line with a call to fetch live exchange rate data
            double currentExchangeRate = getLiveExchangeRate("EUR/USD"); // Replace with the actual pair you want

            exchangeRates[day - 1] = currentExchangeRate;

            // Calculate the SMA indicator value
            double sma = calculateSMA(exchangeRates, day, indicatorPeriod);

            // Determine the trend as bullish or bearish based on the SMA
            String trend = determineTrend(currentExchangeRate, sma);

            double maxAmountToRisk = balance * 0.01; // Risk 1% of the balance
            double stopLossPercentage = (trend.equals("bullish")) ? 0.5 : 1.0;
            double takeProfitPercentage = (trend.equals("bullish")) ? 1.0 : 0.5;
            double stopLossPrice = currentExchangeRate * (1 - stopLossPercentage / 100);
            double takeProfitPrice = currentExchangeRate * (1 + takeProfitPercentage / 100);
            double unitsToTrade = maxAmountToRisk / (currentExchangeRate - stopLossPrice);


            // Place an order to buy or sell based on the trend
            if (trend.equals("bullish")) {
                double tradeCost = unitsToTrade * currentExchangeRate;
                balance -= tradeCost;
                System.out.println("Day " + day + ": Buy " + unitsToTrade + " units at " + currentExchangeRate);
                System.out.println("Stop Loss triggered at " + stopLossPrice);
                balance += unitsToTrade * stopLossPrice;
            } else {
                double tradeRevenue = unitsToTrade * currentExchangeRate;
                balance += tradeRevenue;
                System.out.println("Day " + day + ": Sell " + unitsToTrade + " units at " + currentExchangeRate);
                System.out.println("Take Profit triggered at " + takeProfitPrice);
                balance -= unitsToTrade * takeProfitPrice;
            }
        }

        // Display the final balance
        System.out.println("Final balance: $" + balance);

    }

    // fetch live exchange rate data
    private static double getLiveExchangeRate(String currencyPair) throws IOException {
        double exchangeRate = 0;
        try {
            // Create the URL for the API request
            URL url = new URL(apiEndpoint + "?pair=" + currencyPair);

            // Create an HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Read the response data
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response to extract the exchange rate
                //API response
                JSONObject obj = new JSONObject(response.toString());
                exchangeRate = obj.getJSONObject("conversion_rates").getDouble(currencyPair);

                System.out.println("Live " + currencyPair + " exchange rate: " + exchangeRate);
            } else {
                System.out.println("Error: Unable to retrieve " + currencyPair + " exchange rate. HTTP response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exchangeRate;
    }


    // Calculate the Simple Moving Average (SMA)
    private static double calculateSMA(double[] prices, int currentIndex, int period) {
        if (currentIndex < period) {
            return 0.0; // Not enough data for SMA calculation
        }
        double sum = 0.0;
        for (int i = currentIndex - period; i < currentIndex; i++) {
            sum += prices[i];
        }
        return sum / period;
    }

    // Determine the trend as bullish or bearish based on the SMA and current rate
    private static String determineTrend(double currentRate, double sma) {
        if (currentRate > sma) {
            return "bullish";
        } else {
            return "bearish";
        }
    }
}
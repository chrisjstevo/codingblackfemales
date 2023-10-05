package codingblackfemales.moneymakingalgo;


import java.util.*;


/*
This algo trades in shares and use the simple moving average as an indicator
 */
public class AlgoMakingMoney2 {
    private static double initialBalance;  // Starting balance
    private static double riskPerTrade;    // Risk per trade as a percentage of the account balance
    private static int smaPeriod;            // Period for the Simple Moving Average
    private static double bullishThreshold; // Threshold for considering a bullish trend (SMA crossover)
    private static double bearishThreshold;  // Threshold for considering a bearish trend (SMA crossover)

    private static double balance = initialBalance;
    private static double position;
    private static boolean openTrade;
    private static  List<Double> priceData = new ArrayList<>();//historical data


    public static double SMA(double[] prices, int currentIndex, int period) {
        if (currentIndex < period) {
            return 0.0; // Not enough data for SMA calculation
        }
        double sum = 0.0;
        for (int i = currentIndex - period; i < currentIndex; i++) {
            sum += prices[i];
        }
        double sma = sum / smaPeriod;

        for (int i = smaPeriod; i < priceData.size(); i++) {
            if (priceData.get(i) > sma && priceData.get(i - 1) <= (sma - 1) && !openTrade) {
                // Bullish crossover
                openTrade = true;
                double entryPrice = priceData.get(i);
                double risk = riskPerTrade;
                double sharesToBuy = (balance * risk) / entryPrice;
                balance -= sharesToBuy * entryPrice;
                position += sharesToBuy;
            } else if (priceData.get(i) < sma && priceData.get(i - 1) >= (sma - 1)&& openTrade) {
                // Bearish crossover, close the trade
                openTrade = false;
                double exitPrice = priceData.get(i);
                balance += position * exitPrice;
                position = 0;
            }
        }

        // Calculate final balance
        if (openTrade) {
            // Close the open trade at the last available price
            double exitPrice = priceData.get(priceData.size() - 1);
            balance += position * exitPrice;
        }

        // Print the final balance
        System.out.printf("Final Balance: %.2f%n", balance);
        return balance;
    }
}



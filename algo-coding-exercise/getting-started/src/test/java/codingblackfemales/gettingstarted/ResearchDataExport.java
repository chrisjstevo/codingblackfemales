package codingblackfemales.gettingstarted;

import codingblackfemales.container.RunTrigger;
import codingblackfemales.service.MarketDataService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class ResearchDataExport {

    public static void main(String[] args) throws Exception {
        Path output = Path.of(
                "target", "research", "quotes.csv"
        ).toAbsolutePath();

        Files.createDirectories(output.getParent());

        var market = new MarketDataService(new RunTrigger());
        var random = new Random(2026L);

        double midpoint = 100.0;

        try (var writer = Files.newBufferedWriter(
                output, StandardCharsets.UTF_8)) {

            writer.write(
                    "timestamp_ms,bid,ask,bid_quantity,ask_quantity\n"
            );

            for (int i = 0; i < 5_000; i++) {
                // A synthetic random walk, with a positive-price floor.
                midpoint = Math.max(
                        20.0, midpoint + random.nextGaussian()
                );

                long spread = 1 + random.nextInt(3);
                long bid = (long) Math.floor(midpoint);
                long ask = bid + spread;

                long bidQuantity = 50 + random.nextInt(201);
                long askQuantity = 50 + random.nextInt(201);

                market.onMessage(StretchAlgoTest.marketTick(
                        bid, ask, bidQuantity, askQuantity
                ));

                var decodedBid = market.getBidLevel(0);
                var decodedAsk = market.getAskLevel(0);

                // Exactly one simulated second between observations.
                writer.write(
                        (i * 1_000L) + ","
                                + decodedBid.getPrice() + ","
                                + decodedAsk.getPrice() + ","
                                + decodedBid.getQuantity() + ","
                                + decodedAsk.getQuantity() + "\n"
                );
            }
        }

        System.out.println("Synthetic quotes written to: " + output);
    }
}
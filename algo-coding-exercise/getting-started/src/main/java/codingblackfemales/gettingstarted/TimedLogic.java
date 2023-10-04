package codingblackfemales.gettingstarted;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.marketdata.gen.MarketDataGenerator;
import codingblackfemales.marketdata.gen.RandomMarketDataGenerator;
import codingblackfemales.marketdata.gen.SimpleFileMarketDataGenerator;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.marketdata.Venue;
import messages.order.Side;

public class TimedLogic implements AlgoLogic {

  private static final Logger logger = LoggerFactory.getLogger(TimedLogic.class);
  private SimpleFileMarketDataGenerator marketDataGenerator;
//   private Map<Long, Long> marketMonitor = new HashMap<>();
  private long quantity;// SNIPER
  private long price; // SNIPER
  private MyAlgoLogic myAlgoLogic;
  Map<Long, Long>marketMonitor = new HashMap<>();


   @Override
      public Action evaluate(SimpleAlgoState state) {

        return NoAction.NoAction;
         
        }

     // this method defines the opening and closing times of the stock market. If the market is open the scheduled event is able to run.
        public boolean isMarketOpen(){
            LocalDateTime currentDateTime = LocalDateTime.now();
            DayOfWeek dayOfWeek = currentDateTime.getDayOfWeek();
            LocalTime timeNow = LocalTime.now();
    
            if (dayOfWeek != DayOfWeek.SATURDAY || dayOfWeek != DayOfWeek.SUNDAY){
              LocalTime marketOpen = LocalTime.of(8, 0);
            //   LocalTime marketClosed = LocalTime.of(16, 30);
            LocalTime marketClosed = LocalTime.of(23, 30);
              
                if(timeNow.isBefore(marketOpen) || timeNow.isAfter(marketClosed)){
                logger.info("[TIMEDLOGIC] Market is closed");
                return false;
                    
                } else{
                    logger.info("[TIMEDLOGIC] Market is open");
                    // marketDataGenerator.generate(1);
                    return true;
                }
            }
            return isMarketOpen();
        }

        //schedule data collection schedules a data collection using the simpleFileMarketDataGenerator method as previously seen in code base every 15 into the marketMonitor hashmap. 
        public void scheduleDataCollection(AskLevel farTouch){
            LocalTime currentTime = LocalTime.now();
            // Map<Long, Long>marketMonitor = new HashMap<>();
        final long instrumentId = 1234;
        final Venue venue = Venue.XLON;
        final long priceLevel = 1000;
        final long priceMaxDelta = 100;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable harvestData = () -> {
        
        long price = farTouch.price; // SNIPER
        long quantity = farTouch.quantity;
          
        marketDataGenerator = new SimpleFileMarketDataGenerator("src/main/java/codingblackfemales/gettingstarted/marketdatasimulation.json", new RandomMarketDataGenerator(instrumentId, venue, priceLevel, priceMaxDelta, 15));
        // logger.info("[TIMEDLOGIC] what did it generate " + marketDataGenerator.generate(10));
        marketMonitor.put(price, quantity);
        logger.info("[TIMEDLOGIC] data harvesting");
        System.out.println("Current market monitor list is ");
        marketMonitor.forEach((priceKey, quantityValue) -> {
            System.out.println("yolo Price: " + priceKey + ", Quantity: " + quantityValue + "Current time: " + currentTime);
            logger.info("[Sheza2] market monitor size is " + marketMonitor.size());

        });
        System.out.println("Current market monitor size is " + marketMonitor.size());
        logger.info("[Sheza3] market monitor size is " + marketMonitor.size());

        };
        // ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(harvestData, 0, 15, TimeUnit.MINUTES);
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate( harvestData, 0, 15, TimeUnit.SECONDS);
    //   logger.info("[Sheza4] market monitor size is " + marketMonitor.size());
    }

// checking for trends in the market monitor arrayList. If there are 3 upticks in a row we sell, and for 3 downtick in a row we sell.
        public void checkForTrend() {
        int consecutiveRises = 0;
        int consecutiveFalls = 0;
        // List<Long> prices = new ArrayList<>(marketMonitor.values());
        int listSize = marketMonitor.size();
  
        for(int i = listSize - 1; i >= 2; i--){
          long currentPrice = marketMonitor.get(i);
          long previousPrice = marketMonitor.get(i - 1);
          long thirdPrice = marketMonitor.get(i - 2);
  
          if(currentPrice < previousPrice && previousPrice < thirdPrice){
            consecutiveRises++;
            consecutiveFalls = 0;
          }else if(currentPrice > previousPrice && previousPrice > thirdPrice){
            consecutiveFalls++;
            consecutiveRises = 0; //we reset the counter
          }else{
            consecutiveFalls = 0;
            consecutiveRises = 0;
          }

         long totalQuantity = 0;
         long profit = 0;
          if(consecutiveRises >= 3){
            logger.info("[TIMEDLOGIC] Market trend is rising selling " + quantity + "child orders at " + price);
            totalQuantity -= quantity;
            profit -= price;
            new CreateChildOrder(Side.SELL, quantity, price);
          }else if(consecutiveFalls >= 3){
            logger.info("[TIMEDLOGIC] Market trend is falling buying " + quantity + "child orders at " + price);
            totalQuantity += quantity;
            profit += price;
            new CreateChildOrder(Side.BUY, quantity, price);
          }
          logger.info("[TIMEDLOGIC] Total quantity at the end of this trade is " + totalQuantity + " profit / loss are as follows : " + profit );
        }
    } 
}
//         //   look for event
//         [14:28] Young, Tobyn

// for i in cat market data file

// [14:28] Young, Tobyn

// string (bid p, bid q, ask p, ask q)

// [14:28] Young, Tobyn

// put those in variable

// [14:28] Young, Tobyn

// run your algo

// [14:29] Young, Tobyn

// next
  

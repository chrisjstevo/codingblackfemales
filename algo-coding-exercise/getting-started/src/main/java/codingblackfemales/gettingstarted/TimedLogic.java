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
  
  private long quantity;// SNIPER
  private long price; // SNIPER
  private MyAlgoLogic myAlgoLogic;
  private LocalTime currentTime;
  private DayOfWeek today;

  // hasmap comprised of data harvested from the market every 15 minutes
  Map<Long, Long>marketMonitor = new HashMap<>();

  @Override
  public Action evaluate(SimpleAlgoState state) {
    // final AskLevel farTouch = state.getAskAt(0);
    return NoAction.NoAction;
    }

  
   public TimedLogic(Map<Long, Long>marketMonitor){
    this.marketMonitor = marketMonitor;
  }
  
  public Map<Long, Long> getMarketMonitor() {
    return marketMonitor;
  }

  public void setDayOfWeek(DayOfWeek today){
    this.today = today;
  }

  public void setCurrentTime(LocalTime currentTime){
    this.currentTime = currentTime;
  }

   // this method defines the opening and closing times of the stock market. If the market is open the scheduled event is able to run.
  public boolean isMarketOpen(){
    LocalDateTime currentDateTime = LocalDateTime.now();
    DayOfWeek dayOfWeek = currentDateTime.getDayOfWeek();
    LocalTime timeNow = LocalTime.now();
    
    if (dayOfWeek != DayOfWeek.SATURDAY || dayOfWeek != DayOfWeek.SUNDAY){
      LocalTime marketOpen = LocalTime.of(8, 0);
      LocalTime marketClosed = LocalTime.of(16, 30);
              
        if(timeNow.isBefore(marketOpen) || timeNow.isAfter(marketClosed)){
          logger.info("[TIMEDLOGIC] Market is closed");
          return false;
        } else{
          logger.info("[TIMEDLOGIC] Market is open");
          return true;
        }
      }
    return isMarketOpen();
  }

  //schedule data collection schedules a data collection using the simpleFileMarketDataGenerator method as previously seen in code base every 15 into the marketMonitor hashmap. 
  // scrapping timed logic, i dont have enough time to make it work
  public void scheduleDataCollection(AskLevel farTouch){
 
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    LocalTime currentTime = LocalTime.now();
    Runnable harvestData = () -> {
    
      logger.info("[TIMEDLOGIC] Running scheduler");
      randomLogic(farTouch);
      logger.info("[TIMEDLOGIC] market monitor size in scheduler is " + marketMonitor.size());

    //     if(marketMonitor.size() > 2){
    //       checkForTrend();
    //       logger.info("[TIMEDLOGIC] running check for trend");
    //     }
    };
      // ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(harvestData, 0, 15, TimeUnit.MINUTES);
      ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate( harvestData, 0, 5, TimeUnit.SECONDS);

      if(!isMarketOpen()){
        scheduler.shutdown();
        logger.info("[TIMEDLOGIC] Market is closed, scheduler is shutting down.");
      }
      
  }

    public void randomLogic(AskLevel farTouch){
      logger.info("[TIMEDLOGIC] are we running yet");
    // Map<Long, Long>marketMonitor = new HashMap<>();
    final long instrumentId = 1234;
    final Venue venue = Venue.XLON;
    final long priceLevel = 1000;
    final long priceMaxDelta = 100;

      long price = farTouch.price; // SNIPER
      long quantity = farTouch.quantity;
    
      marketDataGenerator = new SimpleFileMarketDataGenerator("src/main/java/codingblackfemales/gettingstarted/marketdatasimulation.json", new RandomMarketDataGenerator(instrumentId, venue, priceLevel, priceMaxDelta, 15));

      marketMonitor.put(price, quantity);
      logger.info("[TIMEDLOGIC] data harvesting");
      System.out.println("Current market monitor list is ");
      marketMonitor.forEach((priceKey, quantityValue) -> {
        System.out.println("Price: " + priceKey + ", Quantity: " + quantityValue);
        logger.info("[TIMEDLOGIC] market monitor size is " + marketMonitor.size());
      });
  }

  // checking for trends in the market monitor hashmap. If there are 3 upticks in a row we sell, and for 3 downtick in a row we sell.
  public void checkForTrend() {
    int consecutiveRises = 0;
    int consecutiveFalls = 0;
    // List<Long> prices = new ArrayList<>(marketMonitor.values());
    int listSize = marketMonitor.size();

    for(int i = listSize - 1; i >= 2; i--){
      // checks the last 3 items added to marketMonitor hashmap
      long currentPrice = marketMonitor.get(i);
      long previousPrice = marketMonitor.get(i - 1);
      long thirdPrice = marketMonitor.get(i - 2);

      if(currentPrice < previousPrice && previousPrice < thirdPrice){
        // if the trend is going up we add to the consecutive rises and for downward trend we add to consecutiveFalls. we also reset the trend of the variable not experianceing an uptick.
        //we reset the counter to 0 if rise / fall streak is broken.
        consecutiveRises++;
        consecutiveFalls = 0;
      }else if(currentPrice > previousPrice && previousPrice > thirdPrice){
        consecutiveFalls++;
        consecutiveRises = 0; 
      }else{
        consecutiveFalls = 0;
        consecutiveRises = 0;
      }

      long totalQuantity = 0;
      long profit = 0;
      if(consecutiveRises >= 3){
        // for 3 consecutive rises we make a sell order 
        totalQuantity -= quantity;
        profit -= price;
        new CreateChildOrder(Side.SELL, quantity, price);
        logger.info("[TIMEDLOGIC] Market trend is rising selling " + quantity + "child orders at " + price);
        consecutiveRises = 0;
        
      }else if(consecutiveFalls >= 3){
        // for 3 consecutiveFalls we make a buy order 
        totalQuantity += quantity;
        profit += price;
        new CreateChildOrder(Side.BUY, quantity, price);
        logger.info("[TIMEDLOGIC] Market trend is falling buying " + quantity + "child orders at " + price);
        consecutiveFalls = 0;
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
  

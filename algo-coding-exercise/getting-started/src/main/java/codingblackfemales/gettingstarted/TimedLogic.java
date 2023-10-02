package codingblackfemales.gettingstarted;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
import codingblackfemales.util.Util;
import messages.marketdata.Venue;
import messages.order.Side;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimedLogic implements AlgoLogic {

  private static final Logger logger = LoggerFactory.getLogger(TimedLogic.class);
  private SimpleFileMarketDataGenerator marketDataGenerator;

   @Override
      public Action evaluate(SimpleAlgoState state) {

          var orderBookAsString = Util.orderBookToString(state);

        logger.info("[TIMEDLOGIC] The state of the order book is:\n" + orderBookAsString);
        // TODO Auto-generated method stub


         List<Long>marketMonitor = new ArrayList<Long>();

        var totalOrderCount = state.getChildOrders().size();

        final var activeOrders = state.getActiveChildOrders();

        final AskLevel farTouch = state.getAskAt(0);
        // BidLevel level = state.getBidAt(i);
        // AskLevel level = state.getAskAt(i);

        //take as much as we can from the far touch....
        long quantity = farTouch.quantity;
        long latestPrices = 0;

      boolean isMarketOpen = true;

      if(isMarketOpen(isMarketOpen)){
        schduleDataCollection(latestPrices, latestPrices);
      }

      }
  // SimpleAlgoState

      //   // Arraylist comprised of data harvested from the market every 15 minutes

      //create an array for data harvested from market every 15 minutes
    //   check is market open using isMarketOpen()
    // use that to trigger schduleDataCollection() to push data to Arraylist.
    // use checkForTrend(List<Long> marketMonitor) to trigger a buy action or a sell action. methods to be coded in next.


     

      public boolean isMarketOpen(boolean isMarketOpen){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = currentDateTime.getDayOfWeek();
        LocalTime timeNow = LocalTime.now();

        if (dayOfWeek != DayOfWeek.SATURDAY || dayOfWeek != DayOfWeek.SUNDAY){
          LocalTime marketOpen = LocalTime.of(8, 0);
          LocalTime marketClosed = LocalTime.of(16, 30);
          
          if(timeNow.isBefore(marketOpen) || timeNow.isAfter(marketClosed)){
            isMarketOpen = false;
            logger.info("[TIMEDLOGIC] Market is closed");

          }
        }
        return isMarketOpen;
      }

    //   public checkForTrend(List<Long> marketMonitor){
    //   int consecutiveRises = 0;
    //   int consecutiveFalls = 0;
    //   int listSize = marketMonitor.size();

    //   for(int i = listSize - 1; i >= 2; i--){
    //     long currentPrice = marketMonitor.get(i);
    //     long previousPrice = marketMonitor.get(i - 1);
    //     long thirdPrice = marketMonitor.get(i - 2);

    //     if(currentPrice < previousPrice && previousPrice < thirdPrice){
    //       consecutiveRises++;
    //       consecutiveFalls = 0;
    //     }else if(currentPrice > previousPrice && previousPrice > thirdPrice){
    //       consecutiveFalls++;
    //       consecutiveRises = 0; //we rest the counter
    //     }else{
    //       consecutiveFalls = 0;
    //       consecutiveRises = 0;
    //     }

    //     if(consecutiveRises >= 3){
    //         logger.info("[TIMEDLOGIC] Market trend is rising selling " + quantity + "child orders at " + latestPrices);
    //       new CreateChildOrder(Side.SELL, quantity, latestPrices);
    //     }else if(consecutiveFalls >= 3){
    //         logger.info("[TIMEDLOGIC] Market trend is falling buying " + quantity + "child orders at " + latestPrices);
    //       new CreateChildOrder(Side.BUY, quantity, latestPrices);
    //     }
    //   }
    // }

long quantity = farTouch.quantity;
        long latestPrices = 0;
      
      public void schduleDataCollection(/*long quantity, long latestPrices*/){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable checkForTrend = () -> {
          // what is the task we are looking to do at a scheduled time?
          // get latest markets prices/ check latest market trends;

          // find the method that gives you acces to receivin data from the market 
          //  marketDataGenerator = new MarketDataGenerator();
           marketDataGenerator = new SimpleFileMarketDataGenerator("src/test/resources/marketdata.json", new RandomMarketDataGenerator(instrumentId, venue, priceLevel, priceMaxDelta, 15)
          //  may possibly need jacksonobjectMapper to retrieve prices from this api.
          




           checkForTrend(marketMonitor);

          latestPrices = marketDataGenerator;/*this will hold the data that comesback from the market */;
          //find the method;
          
          marketMonitor.add(latestPrices);

          checkForTrend(List<Long>marketMonitor, latestPrices);

          logger.info("[TIMEDLOGIC] latest price on the market is " + latestPrices);

          
        };
          ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(checkForTrend(marketMonitor), 0, 15, TimeUnit.MINUTES);

      }

      private Runnable checkForTrend(List<Long> marketMonitor) {
      int consecutiveRises = 0;
      int consecutiveFalls = 0;
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
          consecutiveRises = 0; //we rest the counter
        }else{
          consecutiveFalls = 0;
          consecutiveRises = 0;
        }

        if(consecutiveRises >= 3){
            logger.info("[TIMEDLOGIC] Market trend is rising selling " + quantity + "child orders at " + latestPrices);
          new CreateChildOrder(Side.SELL, quantity, latestPrices);
        }else if(consecutiveFalls >= 3){
            logger.info("[TIMEDLOGIC] Market trend is falling buying " + quantity + "child orders at " + latestPrices);
          new CreateChildOrder(Side.BUY, quantity, latestPrices);
        }
      }
        return currentPrice;
      }

     
    }

    RandomMarketDataGenerator
    SimpleAlgoStateImpl
    

        MarketDataGeneratorTest
        RandomMarketDataGeneratorTest
        //  algo-coding-exercise\algo\src\test\java\codingblackfemales\marketdata\api\MarketDataGeneratorTest.java
        //  marketDataGenerator = new SimpleFileMarketDataGenerator("src/test/resources/marketdata.json", new RandomMarketDataGenerator(instrumentId, venue, priceLevel, priceMaxDelta, 15));

public class MarketDataGeneratorTest {
    private SimpleFileMarketDataGenerator marketDataGenerator;

    @Before
    public void setup() {
        final long instrumentId = 1234;
        final Venue venue = Venue.XLON;
        final long priceLevel = 1000;
        final long priceMaxDelta = 100;
        marketDataGenerator = new SimpleFileMarketDataGenerator("src/test/resources/marketdata.json", new RandomMarketDataGenerator(instrumentId, venue, priceLevel, priceMaxDelta, 15));
    }

    @Test
    public void should_generate(){
        marketDataGenerator.generate(1000);
        marketDataGenerator.close();
    }





//       public void processMarketDate(){

//       }
//       public void collectData(){
//          // ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//         ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//         long marketUpdate = 0;
//       // you made this class you want it to update with the lastest data when market updates.
//       // you then want to push it to an array and check if last 3 updates were increaing, if so. we sell. if they decrease by e and price is lower than hwta we last sold at. buy again 
//         //new instance of schedule executor service 
//         Runnable harvestMarketData = () -> {

//           long newPrice = //grab new price from incoming market data


//           // AlgoContainer algoContainer = new AlgoContainer(null, null, null, null);

//           // do we think runTrigger is the signal to run the algo and if so can we use this to trigger running the algo every 15 min?
//           // RunTrigger runTrigger = new RunTrigger();

//           RandomMarketDataGenerator randomMarketDataGenerator = new RandomMarketDataGenerator(marketUpdate, null, marketUpdate, marketUpdate, marketUpdate);

//             // this is the task we want to execute every 15 minutes
//             // push data to the marketMonitor array.
//             // i want the result to go into ta variable called market update that can be added to the markeyMonitor arrayList
//             marketUpdate = ?;
           
           
//             scheduler.scheduleAtFixedRate(new.Task(marketUpdate), 0, 15, TimeUnit.MINUTES);
//             // scheduler.schedule(new Task(i), 10 - i,
//             //                    TimeUnit.SECONDS); 
//             marketMonitor.add(marketUpdate);
//       };
      
//   //     Runnable cancelCollection = () -> varName.cancel(false); scheduler.schedule(canceller, 8.5, Hours);
//   //   //   Runnable canceller = () -> beeperHandle.cancel(false);
//   //   //  scheduler.schedule(canceller, 1, HOURS);
//   //  };
       
//   //       };
//     //  marketMonitor.add(marketUpdate);
//     public static void main(String[] args) {
//       // RandomMarketDataGenerator randomMarketDataGenerator = new RandomMarketDataGenerator(, null, marketUpdate, marketUpdate, marketUpdate);
//       // construct a venue and then parse it to random market generator:

//        Venue venue = new Venue(1);
//        venue.value();

//     RandomMarketDataGenerator randomMarketDataGenerator = new RandomMarketDataGenerator(5032, venue, 10, 13, 5);

//     randomMarketDataGenerator.
//     // find / create market data object, do  a loop. if it increases 3 times execute a cell. 
//     // venue is intergers 1 london, 2paris, 3amstadam
  
//   }

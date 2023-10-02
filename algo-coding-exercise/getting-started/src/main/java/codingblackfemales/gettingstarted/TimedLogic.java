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

import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.marketdata.gen.RandomMarketDataGenerator;
import codingblackfemales.sotw.marketdata.AskLevel;
import messages.marketdata.Venue;
import messages.order.Side;

public class TimedLogic {

  Venue venue = new Venue();

      //   // Arraylist comprised of data harvested from the market every 15 minutes

      //create an array for data harvested from market every 15 minutes
    //   check is market open using isMarketOpen()
    // use that to trigger schduleDataCollection() to push data to Arraylist.
    // use checkForTrend(List<Long> marketMonitor) to trigger a buy action or a sell action. methods to be coded in next.


      List<Long>marketMonitor = new ArrayList<Long>();

        var totalOrderCount = state.getChildOrders().size();

        final var activeOrders = state.getActiveChildOrders();

        final AskLevel farTouch = state.getAskAt(0);

        //take as much as we can from the far touch....
        long quantity = farTouch.quantity;
        long price = farTouch.price;

      boolean marketIsOpen = true;

      if(void isMarketOpen()){
        schduleDataCollection();
      }

      public void checkForTrend(List<Long> marketMonitor){
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
          // trigger sell action
        }else if(consecutiveFalls >= 3){
          // trigger buy action
        }
      }
    }

      public boolean isMarketOpen(boolean marketIsOpen){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = currentDateTime.getDayOfWeek();
        LocalTime timeNow = LocalTime.now();

        if (dayOfWeek != DayOfWeek.SATURDAY || dayOfWeek != DayOfWeek.SUNDAY){
          LocalTime marketOpen = LocalTime.of(8, 0);
          LocalTime marketClosed = LocalTime.of(16, 30);
          
          if(timeNow.isBefore(marketOpen) || timeNow.isAfter(marketClosed)){
            marketIsOpen = false;
          }
        }
        return marketIsOpen;
      }

      public void schduleDataCollection(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable harvestMarketData = () -> {
          // what is the task we are looking to do at a scheduled time?
          // get latest markets prices/ check latest market trends;

          // find the method that gives you acces to receivin data from the market 

          long latestPrices = 2020l;
          //find the method;
          logger.info("[MYALGO] latest price on the market is " + latestPrices);

          marketMonitor.add(latestPrices);
        };
          ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(harvestMarketData, 0, 15, TimeUnit.MINUTES);

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



//   // at some point we would like to say 
//   if(marketMonitor index increses 3 times in a row){
//     new CreateChildOrder(Side.SELL, quantity, price);
//   }
//   if(marketsMonitor decreases 3 times in a row){
//     new CreateChildOrder(Side.BUY, quantity, price);
//   }
// }

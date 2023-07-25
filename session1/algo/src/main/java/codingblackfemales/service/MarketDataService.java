package codingblackfemales.service;

import codingblackfemales.container.RunTrigger;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.sequencer.event.MarketDataEventListener;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BidBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketDataService extends MarketDataEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataService.class);

    private int bidLength = 0;
    private int askLength = 0;

    private long instrumentId;
    private final BidLevel[] bidBook = new BidLevel[10];
    private final AskLevel[] askBook = new AskLevel[10];
    private final RunTrigger runTrigger;

    public MarketDataService(RunTrigger runTrigger) {
        this.runTrigger = runTrigger;
    }

    public BidLevel getBidLevel(int i){
        return bidBook[i];
    }

    public AskLevel getAskLevel(int i){
        return askBook[i];
    }

    public int getBidLength(){
        return bidLength;
    }

    public int getAskLength(){
        return askLength;
    }

    public long getInstrumentId(){return instrumentId;}

    private static void empty(BidLevel[] levels){
        for (int i=0; i<levels.length; i++) {
            levels[i] = null;
        }
    }

    private static void empty(AskLevel[] levels){
        for (int i=0; i<levels.length; i++) {
            levels[i] = null;
        }
    }

    @Override
    public void onBookUpdate(BookUpdateDecoder bookUpdate) {

        int bookLevel = 0;

        instrumentId = bookUpdate.instrumentId();

        empty(bidBook);

        for(BookUpdateDecoder.BidBookDecoder decoder : bookUpdate.bidBook()){
            final long price = decoder.price();
            final long quantity = decoder.size();
            bidBook[bookLevel] = new BidLevel();
            bidBook[bookLevel].setPrice(price);
            bidBook[bookLevel].setQuantity(quantity);
            logger.info("BID: price:" + price + " quantity:" + quantity);

            bookLevel+= 1;
            bidLength = bookLevel;

        }

        empty(askBook);

        bookLevel = 0;

        for(BookUpdateDecoder.AskBookDecoder decoder : bookUpdate.askBook()){
            final long price = decoder.price();
            final long quantity = decoder.size();

            askBook[bookLevel] = new AskLevel();
            askBook[bookLevel].setPrice(price);
            askBook[bookLevel].setQuantity(quantity);

            logger.info("ASK: price:" + price + " quantity:" + quantity);

            bookLevel+= 1;
            askLength = bookLevel;
        }

        runTrigger.triggerRun();
    }

    @Override
    public void onAskBook(AskBookUpdateDecoder askBookDec){

        instrumentId = askBookDec.instrumentId();

        empty(askBook);

        int bookLevel = 0;

        for(AskBookUpdateDecoder.AskBookDecoder decoder : askBookDec.askBook()){
            final long price = decoder.price();
            final long quantity = decoder.size();
            askBook[bookLevel] = new AskLevel();
            askBook[bookLevel].setPrice(price);
            askBook[bookLevel].setQuantity(quantity);
            logger.info("BID: price:" + price + " quantity:" + quantity);

            bookLevel+= 1;
            askLength = bookLevel;

        }

        runTrigger.triggerRun();
    }

    @Override
    public void onBidBook(BidBookUpdateDecoder bidBookDec) {
        int bookLevel = 0;

        empty(bidBook);

        instrumentId = bidBookDec.instrumentId();

        for(BidBookUpdateDecoder.BidBookDecoder decoder : bidBookDec.bidBook()){
            final long price = decoder.price();
            final long quantity = decoder.size();
            bidBook[bookLevel] = new BidLevel();
            bidBook[bookLevel].setPrice(price);
            bidBook[bookLevel].setQuantity(quantity);
            logger.info("BID: price:" + price + " quantity:" + quantity);

            bookLevel+= 1;
            bidLength = bookLevel;
        }

        runTrigger.triggerRun();
    }
}

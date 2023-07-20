package codingblackfemales.service;

import codingblackfemales.container.RunTrigger;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.sequencer.event.MarketDataEventListener;
import messages.marketdata.AskBookUpdateDecoder;
import messages.marketdata.BidBookUpdateDecoder;
import messages.marketdata.BookUpdateDecoder;

public class MarketDataService extends MarketDataEventListener {

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

    @Override
    public void onBookUpdate(BookUpdateDecoder bookUpdate) throws Exception {

        int bookLevel = 0;

        instrumentId = bookUpdate.instrumentId();

        for(BookUpdateDecoder.AskBookDecoder decoder : bookUpdate.askBook()){
            final long price = decoder.price();
            final long quantity = decoder.size();

            askBook[bookLevel] = new AskLevel();
            askBook[bookLevel].setPrice(price);
            askBook[bookLevel].setQuantity(quantity);



            System.out.println("ASK: price:" + price + " quantity:" + quantity);

            bookLevel+= 1;
            askLength = bookLevel;
        }

        bookLevel = 0;

        for(BookUpdateDecoder.BidBookDecoder decoder : bookUpdate.bidBook()){
            final long price = decoder.price();
            final long quantity = decoder.size();
            bidBook[bookLevel] = new BidLevel();
            bidBook[bookLevel].setPrice(price);
            bidBook[bookLevel].setQuantity(quantity);
            System.out.println("BID: price:" + price + " quantity:" + quantity);

            bookLevel+= 1;
            bidLength = bookLevel;

        }

        runTrigger.triggerRun();
    }

    @Override
    public void onAskBook(AskBookUpdateDecoder askBookDec) throws Exception {

        instrumentId = askBookDec.instrumentId();

        int bookLevel = 0;

        for(AskBookUpdateDecoder.AskBookDecoder decoder : askBookDec.askBook()){
            final long price = decoder.price();
            final long quantity = decoder.size();
            askBook[bookLevel] = new AskLevel();
            askBook[bookLevel].setPrice(price);
            askBook[bookLevel].setQuantity(quantity);
            System.out.println("BID: price:" + price + " quantity:" + quantity);

            bookLevel+= 1;
            askLength = bookLevel;

        }

        runTrigger.triggerRun();
    }

    @Override
    public void onBidBook(BidBookUpdateDecoder bidBookDec) throws Exception {
        int bookLevel = 0;

        instrumentId = bidBookDec.instrumentId();

        for(BidBookUpdateDecoder.BidBookDecoder decoder : bidBookDec.bidBook()){
            final long price = decoder.price();
            final long quantity = decoder.size();
            bidBook[bookLevel] = new BidLevel();
            bidBook[bookLevel].setPrice(price);
            bidBook[bookLevel].setQuantity(quantity);
            System.out.println("BID: price:" + price + " quantity:" + quantity);

            bookLevel+= 1;
            bidLength = bookLevel;
        }

        runTrigger.triggerRun();
    }
}

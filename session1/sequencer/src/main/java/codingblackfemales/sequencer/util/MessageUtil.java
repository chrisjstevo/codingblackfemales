package codingblackfemales.sequencer.util;

import messages.marketdata.BookUpdateDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtil {

    private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    public static String bookUpdateToString(BookUpdateDecoder decoder){

        final StringBuilder builder = new StringBuilder();

        final BookUpdateDecoder.BidBookDecoder bidBookDecoder = decoder.bidBook();

        int maxLevels = bidBookDecoder.count();// Math.max(askBookDecoder.count(), bidBookDecoder.count());

        builder.append(padLeft("|----BID-----", 12) + "|" + "\n");

        for(int i=0; i<maxLevels; i++){

            if(bidBookDecoder.hasNext()){
                BookUpdateDecoder.BidBookDecoder a = bidBookDecoder.next();
                long size = a.size();
                long price = a.price();
                builder.append(padLeft(size + " @ " + price, 12));
            }else{
                builder.append(padLeft(" - ", 12) + "");
            }

            builder.append("\n");

        }

        builder.append(padLeft("|----ASK-----", 12) + "|"  + "\n");

        final BookUpdateDecoder.AskBookDecoder askBookDecoder = decoder.askBook();

        maxLevels = askBookDecoder.count();

        for(int i=0; i<maxLevels; i++) {

            if (askBookDecoder.hasNext()) {
                BookUpdateDecoder.AskBookDecoder a = askBookDecoder.next();
                long size = a.size();
                long price = a.price();
                builder.append(padLeft(size + " @ " + price, 12));
            } else {
                builder.append(padLeft(" - ", 12) + "");
            }

            builder.append("\n");
        }

        return builder.toString();
    }


}

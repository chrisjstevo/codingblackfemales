package codingblackfemales.util;

import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;

public class Util {

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    public static String orderBookToString(final SimpleAlgoState state){

        final StringBuilder builder = new StringBuilder();

        int maxLevels = Math.max(state.getAskLevels(), state.getBidLevels());

        builder.append(padLeft("|----BID-----", 12) + "|" + padLeft("|----ASK----", 12) + "|" + "\n");

        for(int i=0; i<maxLevels; i++){

            if(state.getBidLevels() > i){
                BidLevel level = state.getBidAt(i);
                builder.append(padLeft(level.quantity + " @ " + level.price, 12));
            }else{
                builder.append(padLeft(" - ", 12) + "");
            }

            if(state.getAskLevels() > i){
                AskLevel level = state.getAskAt(i);
                builder.append(padLeft(level.quantity + " @ " + level.price, 12));
            }else{
                builder.append(padLeft(" - ", 12) + "");
            }

            builder.append("\n");
        }

        builder.append(padLeft("|------------", 12) + "|" + padLeft("|-----------", 12) + "|" + "\n");

        return builder.toString();
    }
}

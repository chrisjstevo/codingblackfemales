package codingblackfemales.marketdata.api;

import codingblackfemales.marketdata.impl.AskBookUpdateImpl;
import codingblackfemales.marketdata.impl.BidBookUpdateImpl;
import codingblackfemales.marketdata.impl.BookUpdateImpl;

public enum UpdateType {
    BookUpdate(BookUpdateImpl.class),
    AskUpdate(AskBookUpdateImpl.class),
    BidUpdate(BidBookUpdateImpl.class),
    ;
    private final Class<? extends MarketDataMessage> messageClass;

    UpdateType(Class<? extends MarketDataMessage> messageClass) {
        this.messageClass = messageClass;
    }

    public Class<? extends MarketDataMessage> getMessageClass() {
        return messageClass;
    }

    public static UpdateType valueOf(int val){
        return UpdateType.values()[val];
    }
}

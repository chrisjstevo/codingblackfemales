package codingblackfemales.sotw;

import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;

import java.util.List;

public interface SimpleAlgoState {

    public String getSymbol();

    public int getBidLevels(); // how many available bid levels
    public int getAskLevels(); // how many available ask levels

    public BidLevel getBidAt(int index); // buyers waiting to buy
    public AskLevel getAskAt(int index); // sellers waiting to sell

    public List<ChildOrder> getChildOrders();

    public List<ChildOrder> getActiveChildOrders();

    public long getInstrumentId();
}

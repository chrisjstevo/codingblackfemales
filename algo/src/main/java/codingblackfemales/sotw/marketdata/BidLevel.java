package codingblackfemales.sotw.marketdata;

public class BidLevel extends AbstractLevel{

    @Override
    public String toString() {
        return "BID[" + getQuantity() + "@" + getPrice() + "]";
    }

}

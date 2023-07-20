package codingblackfemales.orderbook;

import codingblackfemales.collection.intrusive.IntrusiveLinkedListNode;

public class OrderBookLevel extends IntrusiveLinkedListNode<OrderBookLevel> {

    private long price;
    private long quantity;

    public OrderBookLevel() {
        super();
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        if(this.next() == null){
            return "OBLevel(price=" + price + ",quantity=" + quantity + ")" ;
        }else{
            return "OBLevel(price=" + price + ",quantity=" + quantity + ") -> " + this.next().toString();
        }
    }
}

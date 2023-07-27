package codingblackfemales.marketdata.api;

public class BookEntry {
    long price;
    long size;

    public BookEntry() {
    }

    public long price(){
        return price;
    }

    public long size() {
        return size;
    }

    public BookEntry setPrice(long price) {
        this.price = price;
        return this;
    }

    public BookEntry setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public String toString() {
        return "BookEntry{" +
                "price=" + price +
                ", size=" + size +
                '}';
    }
}

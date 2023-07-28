package codingblackfemales.sotw;

import messages.order.Side;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ChildOrder {
    private Side side;
    private long orderId;
    private long quantity;
    private long price;

    private int state;

    private List<ChildFill> fills = new LinkedList<>();

    public ChildOrder(Side side, long orderId, long quantity, long price, int state) {
        this.side = side;
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
        this.state = state;
    }

    public Side getSide() {
        return side;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getPrice() {
        return price;
    }

    public long getFilledQuantity() {
        return fills.stream().map( cf -> cf.getQuantity()).collect(Collectors.summingLong(Long::longValue));
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void addFill(long filledQuantity, long filledPrice) {
        this.fills.add(new ChildFill(filledQuantity, filledPrice));
    }
}

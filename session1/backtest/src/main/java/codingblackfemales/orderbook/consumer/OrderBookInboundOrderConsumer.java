package codingblackfemales.orderbook.consumer;

import codingblackfemales.orderbook.OrderBook;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.sequencer.event.OrderEventListener;
import messages.order.*;

public class OrderBookInboundOrderConsumer extends OrderEventListener {

    private final OrderBook book;

    public OrderBookInboundOrderConsumer(OrderBook book) {
        this.book = book;
    }

    @Override
    public void onCreateOrder(CreateOrderDecoder create) {
        final var limit = new LimitOrderFlyweight(create.side(), create.price(), create.quantity(), create.orderId());
        System.out.println("Adding limit Order:" + limit + " to book");
        book.onLimitOrder(limit);
    }

    @Override
    public void onCancelOrder(CancelOrderDecoder cancel) {

    }

    @Override
    public void onAckedOrder(AckedOrderDecoder acked) {

    }

    @Override
    public void onCancelAckedOrder(CancelAckedOrderDecoder cancelAcked) {

    }

    @Override
    public void onPendingOrder(PendingOrderDecoder pending) {

    }

    @Override
    public void onPartialFill(PartialFillOrderDecoder partialFill) {

    }

    @Override
    public void onFIll(FillOrderDecoder fill) {

    }
}

package codingblackfemales.orderbook.consumer;

import codingblackfemales.orderbook.OrderBook;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.sequencer.event.OrderEventListener;
import messages.order.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBookInboundOrderConsumer extends OrderEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderBookInboundOrderConsumer.class);

    private final OrderBook book;

    public OrderBookInboundOrderConsumer(OrderBook book) {
        this.book = book;
    }

    @Override
    public void onCreateOrder(CreateOrderDecoder create) {
        final var limit = new LimitOrderFlyweight(create.side(), create.price(), create.quantity(), create.orderId());
        //logger.info("Adding limit Order:" + limit + " to book");
        book.onLimitOrder(limit);
    }

    @Override
    public void onCancelOrder(CancelOrderDecoder cancel) {
        book.onCancelOrder(cancel.orderId());
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
    public void onFill(FillOrderDecoder fill) {

    }
}

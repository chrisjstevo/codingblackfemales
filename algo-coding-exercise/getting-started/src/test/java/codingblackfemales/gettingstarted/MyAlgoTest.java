package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import messages.marketdata.*;
import messages.order.Side;
import messages.order.FillOrderEncoder;
import messages.order.PartialFillOrderEncoder;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void createsBuyOrderAtCurrentBestBid() throws Exception {
        send(tick(98, 100, 100, 101));

        assertEquals(1, container.getState().getChildOrders().size());
        ChildOrder order = container.getState().getChildOrders().get(0);
        assertEquals(Side.BUY, order.getSide());
        assertEquals(75, order.getQuantity());
        assertEquals(98, order.getPrice());
    }

    @Test
    public void derivesPriceFromMarketAtADifferentPriceScale() throws Exception {
        send(tick(1500, 100, 1510, 101));

        assertEquals(1, container.getState().getChildOrders().size());
        assertEquals(1500, container.getState().getChildOrders().get(0).getPrice());
    }

    @Test
    public void cancelsAndReplacesOrderWhenBestBidRises() throws Exception {
        send(tick(98, 100, 100, 101));
        send(tick(103, 100, 105, 101));

        assertEquals(2, container.getState().getChildOrders().size());
        assertEquals(OrderState.CANCELLED, container.getState().getChildOrders().get(0).getState());
        assertEquals(103, container.getState().getChildOrders().get(1).getPrice());
        assertEquals(1, container.getState().getActiveChildOrders().size());
    }

    @Test
    public void cancelsAndReplacesOrderWhenBestBidFalls() throws Exception {
        send(tick(98, 100, 100, 101));
        send(tick(95, 100, 100, 101));

        assertEquals(2, container.getState().getChildOrders().size());
        assertEquals(OrderState.CANCELLED, container.getState().getChildOrders().get(0).getState());
        assertEquals(95, container.getState().getChildOrders().get(1).getPrice());
        assertEquals(1, container.getState().getActiveChildOrders().size());
    }

    @Test
    public void keepsOrderWhenOnlyQuantitiesOrAskPriceChange() throws Exception {
        send(tick(98, 100, 100, 101));
        send(tick(98, 200, 100, 300));
        send(tick(98, 200, 105, 300));

        assertEquals(1, container.getState().getChildOrders().size());
        assertEquals(1, container.getState().getActiveChildOrders().size());
    }

    @Test
    public void waitsWhenBidBookIsEmpty() throws Exception {
        send(tick(0, 0, 100, 101));

        assertEquals(0, container.getState().getChildOrders().size());
    }

    @Test
    public void waitsWhenAskBookIsEmpty() throws Exception {
        send(tick(98, 100, 0, 0));

        assertEquals(0, container.getState().getChildOrders().size());
    }

    @Test
    public void waitsWhenPricesOrQuantitiesAreNotPositive() throws Exception {
        send(tick(98, 0, 100, 101));
        send(tick(98, 100, 100, 0));
        send(tick(0, 100, 100, 101));
        send(tick(-1, 100, 100, 101));
        send(tick(98, 100, 0, 101));
        send(tick(98, 100, -1, 101));

        assertEquals(0, container.getState().getChildOrders().size());
    }

    @Test
    public void waitsWhenBookIsLockedOrCrossed() throws Exception {
        send(tick(100, 100, 100, 101));
        send(tick(101, 100, 100, 101));

        assertEquals(0, container.getState().getChildOrders().size());
    }

    @Test
    public void cancelsWhenBidDisappearsAndResumesWhenItReturns() throws Exception {
        send(tick(98, 100, 100, 101));
        send(tick(0, 0, 100, 101));
        assertEquals(OrderState.CANCELLED, container.getState().getChildOrders().get(0).getState());

        send(tick(95, 100, 100, 101));
        assertEquals(2, container.getState().getChildOrders().size());
        assertEquals(95, container.getState().getChildOrders().get(1).getPrice());
    }

    @Test
    public void cancelsWhenAskBookDisappears() throws Exception {
        send(tick(98, 100, 100, 101));
        send(tick(98, 100, 0, 0));

        assertEquals(OrderState.CANCELLED, container.getState().getChildOrders().get(0).getState());
    }

    @Test
    public void stopsCreatingOrdersAfterTargetIsFilled() throws Exception {
        send(tick(98, 100, 100, 101));
        ChildOrder order = container.getState().getChildOrders().get(0);
        send(fill(order.getOrderId(), 75, 98));
        send(tick(103, 100, 105, 101));

        assertEquals(75, order.getFilledQuantity());
        assertEquals(1, container.getState().getChildOrders().size());
    }

    @Test
    public void keepsOutstandingOrderAfterPartialFillAtUnchangedBid() throws Exception {
        send(tick(98, 100, 100, 101));
        ChildOrder order = container.getState().getChildOrders().get(0);
        send(partialFill(order.getOrderId(), 25, 98));
        send(tick(98, 200, 100, 101));

        assertEquals(25, order.getFilledQuantity());
        assertEquals(75, order.getQuantity());
        assertEquals(1, container.getState().getChildOrders().size());
    }

    @Test
    public void replacesOnlyRemainingQuantityAndCountsFillsFromCancelledOrders() throws Exception {
        send(tick(98, 100, 100, 101));
        ChildOrder first = container.getState().getChildOrders().get(0);
        send(partialFill(first.getOrderId(), 25, 98));
        send(tick(95, 100, 100, 101));

        ChildOrder replacement = container.getState().getChildOrders().get(1);
        assertEquals(OrderState.CANCELLED, first.getState());
        assertEquals(25, first.getFilledQuantity());
        assertEquals(95, replacement.getPrice());
        assertEquals(50, replacement.getQuantity());

        send(fill(replacement.getOrderId(), 50, 95));
        send(tick(103, 100, 105, 101));
        assertEquals(50, replacement.getFilledQuantity());
        assertEquals(2, container.getState().getChildOrders().size());
    }

    // These messages test the algo's response to fills without matching orders in the simulator
    private UnsafeBuffer fill(long orderId, long quantity, long price) {
        UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        FillOrderEncoder encoder = new FillOrderEncoder();
        encoder.wrapAndApplyHeader(buffer, 0, new messages.order.MessageHeaderEncoder());
        encoder.orderId(orderId).quantity(quantity).price(price);
        return buffer;
    }

    private UnsafeBuffer partialFill(long orderId, long quantity, long price) {
        UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        PartialFillOrderEncoder encoder = new PartialFillOrderEncoder();
        encoder.wrapAndApplyHeader(buffer, 0, new messages.order.MessageHeaderEncoder());
        encoder.orderId(orderId).quantity(quantity).price(price);
        return buffer;
    }

    private UnsafeBuffer tick(long bidPrice, long bidQuantity, long askPrice, long askQuantity) {
        UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        BookUpdateEncoder encoder = new BookUpdateEncoder();
        encoder.wrapAndApplyHeader(buffer, 0, new MessageHeaderEncoder());
        encoder.venue(Venue.LME).instrumentId(123L)
                .instrumentStatus(InstrumentStatus.CONTINUOUS).source(Source.STREAM);
        // The schema requires bids to be encoded before asks
        if (bidPrice > 0 || bidQuantity > 0) {
            encoder.bidBookCount(1).next().price(bidPrice).size(bidQuantity);
        } else {
            encoder.bidBookCount(0);
        }
        if (askPrice > 0 || askQuantity > 0) {
            encoder.askBookCount(1).next().price(askPrice).size(askQuantity);
        } else {
            encoder.askBookCount(0);
        }
        return buffer;
    }
}

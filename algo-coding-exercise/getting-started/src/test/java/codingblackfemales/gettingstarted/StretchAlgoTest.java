package codingblackfemales.gettingstarted;

import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.OrderState;
import messages.marketdata.BookUpdateEncoder;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.Source;
import messages.marketdata.Venue;
import messages.order.FillOrderEncoder;
import messages.order.PartialFillOrderEncoder;
import messages.order.Side;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class StretchAlgoTest extends AbstractAlgoTest {

    // No initializers here: the superclass calls createAlgoLogic()
    // while constructing the test.
    private long now;
    private StretchAlgoLogic logic;

    @Override
    public AlgoLogic createAlgoLogic() {
        logic = new StretchAlgoLogic(
                () -> now,
                StretchAlgoLogic.EntryMode.TAKE_ASK
        );
        return logic;
    }

    @Test
    public void buysLowAndSellsHigh() throws Exception {
        quote(98, 100);
        assertTrue(container.getState().getChildOrders().isEmpty());

        quote(96, 98);

        var buy = container.getState().getChildOrders().get(0);
        assertEquals(Side.BUY, buy.getSide());
        assertEquals(10L, buy.getQuantity());
        assertEquals(98L, buy.getPrice());
        assertEquals(0L, buy.getFilledQuantity());

        fill(0, 10, 98);

        quote(100, 102);
        assertEquals(1, container.getState().getChildOrders().size());

        quote(102, 104);
        var sell = container.getState().getChildOrders().get(1);

        assertEquals(Side.SELL, sell.getSide());
        assertEquals(10L, sell.getQuantity());
        assertEquals(102L, sell.getPrice());

        fill(1, 10, 102);

        var stats = statistics(102);
        assertEquals(0L, stats.position());
        assertEquals(40.0, stats.realizedGross(), 0.000001);
        assertEquals(4.0, stats.fees(), 0.000001);
        assertEquals(36.0, stats.totalNet(), 0.000001);

        // This instance makes only one round trip.
        quote(96, 98);
        assertEquals(2, container.getState().getChildOrders().size());
    }

    @Test
    public void passivePartialFillExpiresWithoutDuplicateOrders()
            throws Exception {

        usePassiveEntry();
        quote(98, 100);

        // This fill represents four units executed against our
        // resting buy by a compatible incoming seller.
        fill(0, 4, 98);

        for (int i = 0; i < 100; i++) {
            quote(98, 100);
        }

        assertEquals(1, container.getState().getChildOrders().size());

        now = 29_999L;
        assertSame(
                NoAction.NoAction,
                logic.onTimer(container.getState())
        );

        advanceTo(30_000L);

        var buy = container.getState().getChildOrders().get(0);
        assertEquals(OrderState.CANCELLED, buy.getState());
        assertEquals(4L, buy.getFilledQuantity());

        var stats = statistics(98);
        assertEquals(4L, stats.position());
        assertEquals(0.4, stats.quantityFillRate(), 0.000001);

        assertSame(
                NoAction.NoAction,
                logic.onTimer(container.getState())
        );

        quote(102, 104);
        var sell = container.getState().getChildOrders().get(1);

        // Only four units were bought, so only four may be sold.
        assertEquals(4L, sell.getQuantity());

        fill(1, 4, 102);
        assertEquals(0L, statistics(102).position());
        assertEquals(
                16.0, statistics(102).realizedGross(), 0.000001
        );
    }

    @Test
    public void fallingMarketReportsLossWithoutBuyingAgain()
            throws Exception {

        quote(96, 98);
        fill(0, 10, 98);

        for (int i = 0; i < 20; i++) {
            quote(94, 96);
        }

        assertEquals(1, container.getState().getChildOrders().size());

        var stats = statistics(94);
        assertEquals(10L, stats.position());
        assertEquals(0.0, stats.realizedGross(), 0.000001);
        assertEquals(-40.0, stats.unrealizedGross(), 0.000001);

        // Ten purchased units × 0.2 fee = 2.
        assertEquals(-42.0, stats.totalNet(), 0.000001);
    }

    @Test
    public void unfilledBuyExpiresEvenWhenQuotesDisappear()
            throws Exception {

        usePassiveEntry();
        quote(98, 100);
        quote(0, 0);

        advanceTo(30_000L);

        var buy = container.getState().getChildOrders().get(0);
        assertEquals(OrderState.CANCELLED, buy.getState());
        assertEquals(0L, statistics(0).position());

        quote(96, 98);
        assertEquals(1, container.getState().getChildOrders().size());
    }

    @Test
    public void waitsForBuyCompletionBeforeSelling()
            throws Exception {

        quote(96, 98);
        fill(0, 4, 98);
        quote(102, 104);

        assertEquals(1, container.getState().getChildOrders().size());

        // The final six units complete the original buy.
        fill(0, 6, 98);

        assertEquals(2, container.getState().getChildOrders().size());
        assertEquals(
                10L,
                container.getState().getChildOrders().get(1).getQuantity()
        );

        fill(1, 3, 102);
        quote(102, 104);

        assertEquals(2, container.getState().getChildOrders().size());
        assertEquals(7L, statistics(102).position());
    }

    @Test
    public void profitUsesFillPriceInsteadOfOrderLimit()
            throws Exception {

        quote(96, 98);

        // The limit was 98, but this supplied execution report
        // gives the buyer a better actual price of 97.
        fill(0, 10, 97);

        quote(102, 104);
        fill(1, 10, 102);

        assertEquals(
                970L,
                container.getState().getChildOrders()
                        .get(0).getFilledValue()
        );

        var stats = statistics(102);
        assertEquals(97.0, stats.averageBuyPrice(), 0.000001);
        assertEquals(50.0, stats.realizedGross(), 0.000001);
    }

    @Test
    public void invalidOrThinBookDoesNotCreateBuy()
            throws Exception {

        quote(101, 100);
        quote(98, 98);
        quote(0, 98);

        send(marketTick(96, 98, 100, 9));

        assertTrue(container.getState().getChildOrders().isEmpty());
    }

    @Test
    public void sellTimeoutLeavesUnsoldPositionVisible()
            throws Exception {

        quote(96, 98);
        fill(0, 10, 98);

        quote(102, 104);
        fill(1, 3, 102);

        advanceTo(30_000L);

        var sell = container.getState().getChildOrders().get(1);
        assertEquals(OrderState.CANCELLED, sell.getState());

        var stats = statistics(102);
        assertEquals(7L, stats.position());
        assertEquals(12.0, stats.realizedGross(), 0.000001);

        quote(103, 105);
        assertEquals(2, container.getState().getChildOrders().size());
    }

    private void usePassiveEntry() {
        logic = new StretchAlgoLogic(
                () -> now,
                StretchAlgoLogic.EntryMode.PASSIVE_BID
        );
        container.setLogic(logic);
    }

    private void quote(long bid, long ask) throws Exception {
        send(marketTick(bid, ask, 100, 100));
    }

    private void advanceTo(long milliseconds) {
        now = milliseconds;
        logic.onTimer(container.getState())
                .apply(getSequencerInternal());
    }

    private TradeStatistics.Snapshot statistics(long bid) {
        return TradeStatistics.calculate(
                container.getState(), bid, 0.2
        );
    }

    private void fill(int orderIndex, long quantity, long price)
            throws Exception {

        var order = container.getState().getChildOrders()
                .get(orderIndex);

        long remaining =
                order.getQuantity() - order.getFilledQuantity();

        if (quantity <= 0 || quantity > remaining || price <= 0) {
            throw new IllegalArgumentException("Invalid test fill");
        }

        if ((order.getSide() == Side.BUY && price > order.getPrice())
                || (order.getSide() == Side.SELL
                && price < order.getPrice())) {
            throw new IllegalArgumentException(
                    "Test fill violates the order's limit"
            );
        }

        var buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        var header = new messages.order.MessageHeaderEncoder();

        if (quantity < remaining) {
            var encoder = new PartialFillOrderEncoder();
            encoder.wrapAndApplyHeader(buffer, 0, header);
            encoder.orderId(order.getOrderId());
            encoder.quantity(quantity);
            encoder.price(price);
        } else {
            var encoder = new FillOrderEncoder();
            encoder.wrapAndApplyHeader(buffer, 0, header);
            encoder.orderId(order.getOrderId());
            encoder.quantity(quantity);
            encoder.price(price);
        }

        send(buffer);
    }

    static UnsafeBuffer marketTick(
            long bid,
            long ask,
            long bidQuantity,
            long askQuantity
    ) {
        var buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        var encoder = new BookUpdateEncoder();

        encoder.wrapAndApplyHeader(
                buffer, 0,
                new messages.marketdata.MessageHeaderEncoder()
        );

        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);
        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        // The repository's message schema requires bids before asks.
        var bids = encoder.bidBookCount(bid == 0 ? 0 : 1);
        if (bid != 0) {
            bids.next().price(bid).size(bidQuantity);
        }

        var asks = encoder.askBookCount(ask == 0 ? 0 : 1);
        if (ask != 0) {
            asks.next().price(ask).size(askQuantity);
        }

        return buffer;
    }
}
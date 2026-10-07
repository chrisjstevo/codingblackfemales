package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import org.junit.Test;


/**
 * This test is designed to check your algo behavior in isolation of the order book.
 *
 * You can tick in market data messages by creating new versions of createTick() (ex. createTick2, createTickMore etc..)
 *
 * You should then add behaviour to your algo to respond to that market data by creating or cancelling child orders.
 *
 * When you are comfortable you algo does what you expect, then you can move on to creating the MyAlgoBackTest.
 *
 */

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.OrderState;
import messages.marketdata.BookUpdateEncoder;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.MessageHeaderEncoder;
import messages.marketdata.Source;
import messages.marketdata.Venue;
import messages.order.Side;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void createsOneBuyOrderAtBestBid() throws Exception {
        send(marketTick(98L, 100L));

        var orders = container.getState().getChildOrders();

        assertEquals("Exactly one order should exist", 1, orders.size());

        var order = orders.get(0);

        assertEquals(Side.BUY, order.getSide());
        assertEquals(10L, order.getQuantity());
        assertEquals(98L, order.getPrice());
        assertEquals(1, container.getState().getActiveChildOrders().size());
    }

    @Test
    public void unchangedBidDoesNotDuplicateOrCancelOrder() throws Exception {
        send(marketTick(98L, 100L));
        send(marketTick(98L, 100L));
        send(marketTick(98L, 101L));

        assertEquals(
                "Repeated updates should not create extra orders",
                1,
                container.getState().getChildOrders().size()
        );

        assertEquals(
                "The order should remain active while the bid is unchanged",
                1,
                container.getState().getActiveChildOrders().size()
        );
    }

    @Test
    public void fallingBidCancelsOrder() throws Exception {
        send(marketTick(98L, 100L));
        send(marketTick(95L, 97L));

        assertSingleCancelledOrder();
    }

    @Test
    public void risingBidCancelsOrder() throws Exception {
        send(marketTick(98L, 100L));
        send(marketTick(99L, 101L));

        assertSingleCancelledOrder();
    }

    @Test
    public void cancelledOrderIsNotReplaced() throws Exception {
        send(marketTick(98L, 100L));
        send(marketTick(95L, 97L));
        send(marketTick(98L, 100L));
        send(marketTick(99L, 101L));

        assertSingleCancelledOrder();
    }

    @Test
    public void crossedMarketDoesNotCreateAnOrder() throws Exception {
        // A bid above the ask is unsuitable for our entry rule.
        send(marketTick(101L, 100L));

        assertTrue(
                "No order should be created for a crossed market",
                container.getState().getChildOrders().isEmpty()
        );
    }

    @Test
    public void waitsForBothSidesOfTheMarket() throws Exception {
        // Zero means that this side has no price levels.
        send(marketTick(0L, 0L));
        send(marketTick(98L, 0L));

        assertTrue(
                "No order should be created with missing market data",
                container.getState().getChildOrders().isEmpty()
        );

        // Once both sides arrive, the algorithm can place its order.
        send(marketTick(98L, 100L));

        assertEquals(
                1,
                container.getState().getChildOrders().size()
        );
    }

    private void assertSingleCancelledOrder() {
        var orders = container.getState().getChildOrders();

        assertEquals(
                "The original order should remain in the order history",
                1,
                orders.size()
        );

        assertEquals(
                "The original order should be marked cancelled",
                OrderState.CANCELLED,
                orders.get(0).getState()
        );

        assertTrue(
                "No active orders should remain",
                container.getState().getActiveChildOrders().isEmpty()
        );
    }

    private UnsafeBuffer marketTick(long bidPrice, long askPrice) {
        var buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        var header = new MessageHeaderEncoder();
        var encoder = new BookUpdateEncoder();

        encoder.wrapAndApplyHeader(buffer, 0, header);
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);
        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        // The message format requires bids before asks.
        if (bidPrice == 0L) {
            encoder.bidBookCount(0);
        } else {
            encoder.bidBookCount(1)
                    .next()
                    .price(bidPrice)
                    .size(100L);
        }

        if (askPrice == 0L) {
            encoder.askBookCount(0);
        } else {
            encoder.askBookCount(1)
                    .next()
                    .price(askPrice)
                    .size(100L);
        }

        return buffer;
    }
}

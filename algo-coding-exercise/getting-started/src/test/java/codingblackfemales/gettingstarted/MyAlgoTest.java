package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    /**
     * Builds a market data tick with the given number of price levels.
     * topPrice is the best price the algo will see on its bid side.
     * levels = 0 gives an empty book.
     */
    private UnsafeBuffer createTickAt(int levels, long topPrice) {
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();
        final UnsafeBuffer directBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        // this side is the one the algo reads as its bids (see the log output)
        var bidSide = encoder.askBookCount(levels);
        for (int i = 0; i < levels; i++) {
            bidSide.next().price(topPrice + (i * 5L)).size(100L);
        }

        // the other side, set lower
        var otherSide = encoder.bidBookCount(levels);
        for (int i = 0; i < levels; i++) {
            otherSide.next().price(topPrice - 2 - (i * 5L)).size(100L);
        }

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);
        return directBuffer;
    }

    // Scenario 1: normal market. 3 orders created in total, 1 cancelled.
    @Test
    public void testDispatchThroughSequencer() throws Exception {
        send(createTick());

        assertEquals(3, container.getState().getChildOrders().size());
        assertEquals(2, container.getState().getActiveChildOrders().size());
    }

    // Scenario 2: empty order book. Nothing to price against, so no orders.
    @Test
    public void testEmptyBookCreatesNoOrders() throws Exception {
        send(createTickAt(0, 0));

        assertEquals(0, container.getState().getChildOrders().size());
    }

    // Scenario 3: price falls after the orders are placed. Algo creates nothing new.
    @Test
    public void testPriceFallsAfterOrdersPlaced() throws Exception {
        send(createTickAt(3, 100));
        send(createTickAt(3, 90));

        assertEquals(3, container.getState().getChildOrders().size());
        assertEquals(2, container.getState().getActiveChildOrders().size());
    }

    // Scenario 4: price rises after the orders are placed. Algo creates nothing new.
    @Test
    public void testPriceRisesAfterOrdersPlaced() throws Exception {
        send(createTickAt(3, 100));
        send(createTickAt(3, 120));

        assertEquals(3, container.getState().getChildOrders().size());
        assertEquals(2, container.getState().getActiveChildOrders().size());
    }
}
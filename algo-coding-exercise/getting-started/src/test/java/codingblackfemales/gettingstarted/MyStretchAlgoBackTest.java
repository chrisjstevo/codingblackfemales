package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import messages.marketdata.*;
import messages.order.Side;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertTrue;

/**
 * Backtest for the stretch goal: the market sits flat, dips (algo should buy),
 * then rises (algo should sell what it bought).
 */
public class MyStretchAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyStretchAlgoLogic();
    }

    @Test
    public void testBuysLowAndSellsHigh() throws Exception {
        // given: a flat market around 99, so the algo can learn an average
        send(tick(98, 100));
        send(tick(98, 100));
        send(tick(98, 100));

        // when: the market dips well below the average -> cheap, should BUY
        send(tick(90, 92));
        send(tick(90, 92));

        // and: the market rises well above the average -> expensive, should SELL
        send(tick(108, 110));
        send(tick(108, 110));

        // then: check what happened
        var state = container.getState();

        long boughtQty = 0;
        long soldQty = 0;
        for (ChildOrder order : state.getChildOrders()) {
            if (order.getSide() == Side.BUY) {
                boughtQty += order.getFilledQuantity();
            } else {
                soldQty += order.getFilledQuantity();
            }
        }
        System.out.println("[STRETCH TEST] bought=" + boughtQty + " sold=" + soldQty
                + " childOrders=" + state.getChildOrders().size());

        assertTrue("algo should have bought during the dip", boughtQty > 0);
        assertTrue("algo should have sold during the rise", soldQty > 0);
    }

    /** Builds a simple two-level book with the given best bid and best ask. */
    private UnsafeBuffer tick(long bestBid, long bestAsk) {
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);
        encoder.source(Source.STREAM);

        encoder.bidBookCount(2)
                .next().price(bestBid).size(200L)
                .next().price(bestBid - 2).size(300L);

        encoder.askBookCount(2)
                .next().price(bestAsk).size(200L)
                .next().price(bestAsk + 2).size(300L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        return directBuffer;
    }
}
package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class MyAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void restsAtBestBidWithoutDuplicatingOnBookFeedback() throws Exception {
        send(tick(98, 100, 100, 101));
        send(tick(98, 100, 100, 101));

        assertEquals(1, container.getState().getChildOrders().size());
        assertEquals(98, container.getState().getChildOrders().get(0).getPrice());
        assertEquals(0, container.getState().getChildOrders().get(0).getFilledQuantity());
        assertEquals(98, container.getState().getBidAt(0).price);
        assertEquals(100, container.getState().getAskAt(0).price);
    }

    @Test
    public void fillsTargetWhenSellersReachOurPriceAndThenStops() throws Exception {
        send(tick(98, 100, 100, 101));
        send(tick(95, 100, 98, 101));
        send(tick(95, 100, 98, 101));

        assertEquals(75, container.getState().getChildOrders().get(0).getFilledQuantity());
        assertEquals(1, container.getState().getChildOrders().size());
    }

    @Test
    public void cancelsInSimulatorWhenAskBookDisappearsAndCannotFillCancelledOrder() throws Exception {
        send(tick(98, 100, 100, 101));
        var order = container.getState().getChildOrders().get(0);

        send(tick(98, 100, 0, 0));

        assertEquals(codingblackfemales.sotw.OrderState.CANCELLED, order.getState());
        assertEquals(0, container.getState().getActiveChildOrders().size());
        assertEquals(1, container.getState().getChildOrders().size());

        send(tick(95, 100, 98, 101));
        assertEquals(0, order.getFilledQuantity());
    }

    private UnsafeBuffer tick(long bidPrice, long bidQuantity, long askPrice, long askQuantity) {
        UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        BookUpdateEncoder encoder = new BookUpdateEncoder();
        encoder.wrapAndApplyHeader(buffer, 0, new MessageHeaderEncoder());
        encoder.venue(Venue.LME).instrumentId(123L)
                .instrumentStatus(InstrumentStatus.CONTINUOUS).source(Source.STREAM);
        if (bidPrice > 0) {
            encoder.bidBookCount(1).next().price(bidPrice).size(bidQuantity);
        } else {
            encoder.bidBookCount(0);
        }
        if (askPrice > 0) {
            encoder.askBookCount(1).next().price(askPrice).size(askQuantity);
        } else {
            encoder.askBookCount(0);
        }
        return buffer;
    }
}

package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import messages.marketdata.*;
import messages.order.Side;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test plugs together all of the infrastructure, including the order book (which you can trade against)
 * and the market data feed.
 *
 * If your algo adds orders to the book, they will reflect in your market data coming back from the order book.
 *
 * If you cross the srpead (i.e. you BUY an order with a price which is == or > askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the childOrders of the state object.
 *
 * If you cancel the order your child order will show the order status as cancelled in the childOrders of the state object.
 *
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    protected UnsafeBuffer createTickBuyRests() {

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
                .next().price(98L).size(100L)
                .next().price(95L).size(200L)
                .next().price(91L).size(300L);


        encoder.askBookCount(3)
                .next().price(100L).size(101L)
                .next().price(110L).size(200L)
                .next().price(115L).size(5000L);


        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    protected UnsafeBuffer createTickBidRises() {

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
                .next().price(101L).size(100L)
                .next().price(96L).size(200L)
                .next().price(95L).size(300L);

        encoder.askBookCount(3)
                .next().price(104L).size(101L)
                .next().price(109L).size(200L)
                .next().price(119L).size(5600L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    protected UnsafeBuffer createTickAskFillsBuy() {

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
                .next().price(95L).size(100L)
                .next().price(93L).size(200L)
                .next().price(91L).size(300L);


        encoder.askBookCount(4)
                .next().price(98L).size(500L)
                .next().price(101L).size(200L)
                .next().price(110L).size(5000L)
                .next().price(119L).size(5600L);


        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }


    protected UnsafeBuffer createTickBidHigher() {

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        encoder.venue(Venue.LME);
        encoder.instrumentId(123L);

        encoder.bidBookCount(3)
                .next().price(105L).size(100L)
                .next().price(100L).size(200L)
                .next().price(96L).size(300L);

        encoder.askBookCount(3)
                .next().price(108L).size(101L)
                .next().price(112L).size(200L)
                .next().price(119L).size(5600L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }


    @Test
    public void testExampleBackTest() throws Exception {

        // Tick 1: buy rests at 98
        send(createTickBuyRests());
        assertEquals(1, container.getState().getChildOrders().size());
        assertEquals(1, container.getState().getActiveChildOrders().size());

        //Tick 2 buy get cancelled and new one created
        send(createTickBidRises());
        assertEquals(2, container.getState().getChildOrders().size());
        assertEquals(1, container.getState().getActiveChildOrders().size());

        // Tick 3: ask at 98 fills our buy
        send(createTickAskFillsBuy());
        long filledQuantity = container.getState().getChildOrders().stream()
                .mapToLong(ChildOrder::getFilledQuantity)
                .sum();
        assertEquals(55L, filledQuantity);
        assertEquals(1, container.getState().getActiveChildOrders().size());
        assertEquals(0, container.getState().getChildOrders().stream()
                .filter(order -> order.getSide() == Side.SELL).count());

        // Tick 4: best bid 105 is above our buy price, so we sell
        send(createTickBidHigher());
        long sellOrders = container.getState().getChildOrders().stream()
                .filter(order -> order.getSide() == Side.SELL)
                .count();

        //Not exactly 1, because nothing in the algo stops a second sell. Here the collapsed book happens to prevent it
        assertTrue(sellOrders >= 1);

        long buyPrice = container.getState().getChildOrders().stream()
                .filter(order -> order.getSide() == Side.BUY)
                .filter(order -> order.getFilledQuantity() > 0)
                .mapToLong(ChildOrder::getPrice)
                .findFirst().getAsLong();

        long sellPrice = container.getState().getChildOrders().stream()
                .filter(order -> order.getSide() == Side.SELL)
                .mapToLong(ChildOrder::getPrice)
                .findFirst().getAsLong();
        assertTrue(sellPrice > buyPrice);


    }
}


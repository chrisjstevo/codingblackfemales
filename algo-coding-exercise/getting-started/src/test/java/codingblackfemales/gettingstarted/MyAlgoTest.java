package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.util.Util;
import messages.marketdata.BookUpdateEncoder;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.MessageHeaderEncoder;
import messages.marketdata.Source;
import messages.marketdata.Venue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }


    @Test
    public void testDispatchThroughSequencer() throws Exception {

        send(createTick());
        assertEquals(container.getState().getChildOrders().size(), 0);
        assertNotNull(createTick());
        
    }

    protected UnsafeBuffer createTickTwo(){

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

     
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);

        encoder.askBookCount(3)
            .next().price(110L).size(5000L)
            .next().price(113L).size(3000L)
            .next().price(116L).size(1000L);


        encoder.bidBookCount(3)
            .next().price(112L).size(3000L)
            .next().price(115L).size(1500L)
            .next().price(118L).size(2500L);


        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

        @Test
    public void testDispatchThroughSequencerTwo() throws Exception {

       
        send(createTickTwo());
        assertEquals(container.getState().getChildOrders().size(), 0);
        assertNotNull(createTickTwo());
        
    }

    
    protected UnsafeBuffer createTickThree(){

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

     
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);

        encoder.askBookCount(6)
            .next().price(110L).size(500L)
            .next().price(111L).size(1000L)
            .next().price(112L).size(1500L)
            .next().price(113L).size(2000L)
            .next().price(114L).size(2500L)
            .next().price(115L).size(3000L);


        encoder.bidBookCount(6)
            .next().price(108L).size(500L)
            .next().price(107L).size(1000L)
            .next().price(106L).size(1500L)
            .next().price(105L).size(2000L)
            .next().price(104L).size(2500L)
            .next().price(103L).size(3000L);


        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

        @Test
    public void testDispatchThroughSequencerThree() throws Exception {

       
        send(createTickThree());
        assertEquals(container.getState().getChildOrders().size(), 3);
        assertNotNull(createTickThree());
        
    }

        protected UnsafeBuffer createTickFour(){

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

     
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);

        encoder.askBookCount(4)
            .next().price(97L).size(5000L)
            .next().price(99L).size(10000L)
            .next().price(101L).size(15000L)
            .next().price(103L).size(20000L);


        encoder.bidBookCount(4)
            .next().price(100L).size(6000L)
            .next().price(102L).size(9000L)
            .next().price(104L).size(7000L)
            .next().price(106L).size(5000L);



        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

        @Test
    public void testDispatchThroughSequencerFour() throws Exception {

       
        send(createTickFour());
        assertEquals(container.getState().getChildOrders().size(), 3);
        assertNotNull(createTickFour());
        
    }  
}
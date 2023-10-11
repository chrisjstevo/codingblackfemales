package codingblackfemales.sequencer.marketdata;

import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.consumer.LoggingConsumer;
import codingblackfemales.sequencer.net.TestNetwork;
import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;
public class BasicMarketDataSequencerTest extends SequencerTestCase {

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder encoder = new BookUpdateEncoder();
    private final BookUpdateDecoder decoder = new BookUpdateDecoder();

    @Override
    public Sequencer getSequencer() {
        final TestNetwork network = new TestNetwork();
        final Sequencer sequencer = new DefaultSequencer(network);
        network.addConsumer(new LoggingConsumer());
        return sequencer;
    }

    @Test
    public void testDispatchThroughSequencer() throws Exception {

        //Allocate memory and an unsafe buffer to act as a destination for the data
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);

        encoder.askBookCount(3)
                .next().price(100l).size(101l)
                .next().price(90l).size(200l)
                .next().price(80l).size(300);

        encoder.bidBookCount(3)
                .next().price(110l).size(100)
                .next().price(210l).size(200)
                .next().price(310l).size(300);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        send(directBuffer);
    }


}

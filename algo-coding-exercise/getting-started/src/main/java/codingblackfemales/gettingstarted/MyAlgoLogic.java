package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.util.Util;
import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.RealSequencer;


public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder bookUpdateEncoder = new BookUpdateEncoder();

    private UnsafeBuffer createTick2(){
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        bookUpdateEncoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        //set the fields to desired values
        bookUpdateEncoder.venue(Venue.XLON);
        bookUpdateEncoder.instrumentId(123L);
        bookUpdateEncoder.source(Source.STREAM);

        bookUpdateEncoder.bidBookCount(3)
                .next().price(100L).size(100L)
                .next().price(96L).size(200L)
                .next().price(93L).size(300L);

        bookUpdateEncoder.askBookCount(4)
                .next().price(101L).size(101L)
                .next().price(115L).size(200L)
                .next().price(120L).size(5000L)
                .next().price(130L).size(5000L);

        bookUpdateEncoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        return directBuffer;
    }



    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);
        /********
         ** .
         *  * You can tick in market data messages by creating new versions of createTick() (ex. createTick2, createTickMore etc..)
         *  * You should then add behaviour to your algo to respond to that market data by creating or cancelling child orders.
         *  *
         *
         *
         *
         *
         *
         */


        return NoAction.NoAction;
    }
}

package codingblackfemales.sequencer;

import codingblackfemales.sequencer.util.ByteBufferUtil;
import messages.marketdata.MessageHeaderEncoder;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class RealSequencer {

    final ByteBuffer sequenced = ByteBuffer.allocateDirect(1024);
    final UnsafeBuffer directBuffer = new UnsafeBuffer();
    final MessageHeaderEncoder encoder = new MessageHeaderEncoder();

    final long sequenceNumber = 0L;

    public void processMessage(DirectBuffer buffer){

        ByteBufferUtil.cloneInto(buffer.byteBuffer(), sequenced);

        directBuffer.wrap(sequenced);

        encoder.wrap(directBuffer, 0);

        encoder.sequencerNumber(sequenceNumber);

        dispatch(directBuffer);
    }

    public void dispatch(UnsafeBuffer buffer){
        //TODO: Implement
    }

}

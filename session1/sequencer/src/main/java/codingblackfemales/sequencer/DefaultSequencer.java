package codingblackfemales.sequencer;

import codingblackfemales.sequencer.net.Network;
import messages.marketdata.MessageHeaderDecoder;
import messages.marketdata.MessageHeaderEncoder;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class DefaultSequencer implements Sequencer {

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();

    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    private final UnsafeBuffer mutableBuffer = new UnsafeBuffer(byteBuffer);

    long sequencerNumber = 0L;

    private final Network network;

    public DefaultSequencer(Network network) {
        this.network = network;
    }

    @Override
    public void onCommand(DirectBuffer byteBuffer) throws Exception {

        headerDecoder.wrap(byteBuffer, 0);

        int schemaId = headerDecoder.schemaId();
        int version = headerDecoder.version();

        if(isModelMessage(schemaId)){
            processModelCommand(byteBuffer, schemaId, headerDecoder);
        }

        sequenceAndDispatchMessage(byteBuffer);
    }

    public void processModelCommand(final DirectBuffer byteBuffer, final int schemaId, final MessageHeaderDecoder headerDecoder){
        //todo: if we had to validate the order for example, we'd go up into this workflow
    }

    public void sequenceAndDispatchMessage(final DirectBuffer byteBuffer) throws Exception{

        mutableBuffer.wrap(byteBuffer);

        headerEncoder.wrap(mutableBuffer, 0);

        sequencerNumber += 1;

        headerEncoder.sequencerNumber(sequencerNumber);

        dispatchToNetwork(mutableBuffer);
    }

    public void dispatchToNetwork(DirectBuffer sequencedBuffer) throws Exception{
        network.dispatch(sequencedBuffer);
    }

    public boolean isModelMessage(final int schemaId) {
        return false;
    }

}

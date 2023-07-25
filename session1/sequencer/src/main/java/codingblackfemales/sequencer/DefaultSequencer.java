package codingblackfemales.sequencer;

import codingblackfemales.sequencer.net.Network;
import messages.marketdata.MessageHeaderDecoder;
import messages.marketdata.MessageHeaderEncoder;
import messages.order.CreateOrderDecoder;
import messages.order.CreateOrderEncoder;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class DefaultSequencer implements Sequencer {

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();

    private ByteBuffer byteBuffer;//= ByteBuffer.allocateDirect(1024);
    private UnsafeBuffer mutableBuffer;// = new UnsafeBuffer(byteBuffer);

    private final CreateOrderDecoder createOrderDecoder = new CreateOrderDecoder();
    private final CreateOrderEncoder createOrderEncoder = new CreateOrderEncoder();

    private final messages.order.MessageHeaderEncoder businessHeaderEncoder = new messages.order.MessageHeaderEncoder();

    private final ByteBuffer businessByteBuffer = ByteBuffer.allocateDirect(1024);
    private final UnsafeBuffer businessMutableBuffer = new UnsafeBuffer(businessByteBuffer);

    long sequencerNumber = 0L;

    private final Network network;

    public DefaultSequencer(Network network) {
        this.network = network;
    }

    @Override
    public void onCommand(DirectBuffer bb) {

        headerDecoder.wrap(bb, 0);

        int schemaId = headerDecoder.schemaId();
        int templateId = headerDecoder.templateId();

        if(isModelMessage(schemaId, templateId)){
            DirectBuffer mutatedBuffer = processModelCommand(bb, schemaId, headerDecoder);
            sequenceAndDispatchMessage(mutatedBuffer);
        }
        else{
            sequenceAndDispatchMessage(bb);
        }

    }

    public DirectBuffer processModelCommand(final DirectBuffer byteBuffer, final int schemaId, final MessageHeaderDecoder header){

        final int actingBlockLength = header.blockLength();
        final int actingVersion = header.version();
        final int bufferOffset = header.encodedLength();

        createOrderDecoder.wrap(byteBuffer, bufferOffset, actingBlockLength, actingVersion);


        createOrderEncoder.wrapAndApplyHeader(businessMutableBuffer, 0, businessHeaderEncoder);
        createOrderEncoder.price(createOrderDecoder.price());
        createOrderEncoder.quantity(createOrderDecoder.quantity());
        createOrderEncoder.side(createOrderDecoder.side());
        createOrderEncoder.orderId(newOrderId());
        return businessMutableBuffer;
    }

    private long orderId = 1;

    public long newOrderId(){
        return orderId +=1;
    }

    public void sequenceAndDispatchMessage(final DirectBuffer bb){

        byteBuffer = ByteBuffer.allocateDirect(1024);
        mutableBuffer = new UnsafeBuffer(byteBuffer);

        mutableBuffer.wrap(bb);

        headerEncoder.wrap(mutableBuffer, 0);

        sequencerNumber += 1;

        headerEncoder.sequencerNumber(sequencerNumber);

        dispatchToNetwork(mutableBuffer);
    }

    public void dispatchToNetwork(DirectBuffer sequencedBuffer){
        network.dispatch(sequencedBuffer);
    }

    public boolean isModelMessage(final int schemaId, final int templateId) {
        return schemaId == CreateOrderEncoder.SCHEMA_ID && templateId == CreateOrderEncoder.TEMPLATE_ID;
    }

}

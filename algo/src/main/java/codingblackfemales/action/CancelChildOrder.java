package codingblackfemales.action;

import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sotw.ChildOrder;
import messages.order.CancelOrderEncoder;
import messages.order.MessageHeaderEncoder;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class CancelChildOrder implements Action{

    private final ChildOrder orderToCancel;

    public CancelChildOrder(ChildOrder orderToCancel) {
        this.orderToCancel = orderToCancel;
    }

    @Override
    public String toString() {
        return "CancelChildOrder(" + orderToCancel + ")";
    }

    @Override
    public void apply(final Sequencer sequencer) {

        final CancelOrderEncoder encoder = new CancelOrderEncoder();
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        headerEncoder.schemaId(CancelOrderEncoder.SCHEMA_ID);
        headerEncoder.version(CancelOrderEncoder.SCHEMA_VERSION);

        encoder.orderId(orderToCancel.getOrderId());

        sequencer.onCommand(directBuffer);
    }
}

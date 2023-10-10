package codingblackfemales.sequencer.event;

import codingblackfemales.sequencer.net.Consumer;
import messages.marketdata.MessageHeaderDecoder;
import messages.order.*;
import org.agrona.DirectBuffer;

public abstract class OrderEventListener implements Consumer {

    private final MessageHeaderDecoder header = new MessageHeaderDecoder();

    private final CreateOrderDecoder createOrderDecoder = new CreateOrderDecoder();
    private final CancelOrderDecoder cancelOrderDecoder = new CancelOrderDecoder();
    private final AckedOrderDecoder ackedOrderDecoder = new AckedOrderDecoder();
    private final CancelAckedOrderDecoder cancelAckedOrderDecoder = new CancelAckedOrderDecoder();
    private final PendingOrderDecoder pendingOrderDecoder = new PendingOrderDecoder();
    private final PartialFillOrderDecoder partialFillOrderDecoder = new PartialFillOrderDecoder();
    private final FillOrderDecoder fillOrderDecoder = new FillOrderDecoder();

    @Override
    public void onMessage(DirectBuffer buffer){

        header.wrap(buffer, 0);

        final int actingBlockLength = header.blockLength();
        final int actingVersion = header.version();
        final int bufferOffset = header.encodedLength();

        if(header.schemaId() == CreateOrderEncoder.SCHEMA_ID) {

            if (header.templateId() == CreateOrderDecoder.TEMPLATE_ID) {
                createOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
                onCreateOrder(createOrderDecoder);
            } else if (header.templateId() == CancelOrderDecoder.TEMPLATE_ID) {
                cancelOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
                onCancelOrder(cancelOrderDecoder);
            } else if (header.templateId() == AckedOrderDecoder.TEMPLATE_ID) {
                ackedOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
                onAckedOrder(ackedOrderDecoder);
            } else if (header.templateId() == CancelAckedOrderDecoder.TEMPLATE_ID) {
                cancelAckedOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
                onCancelAckedOrder(cancelAckedOrderDecoder);
            } else if (header.templateId() == PendingOrderDecoder.TEMPLATE_ID) {
                pendingOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
                onPendingOrder(pendingOrderDecoder);
            } else if (header.templateId() == PartialFillOrderDecoder.TEMPLATE_ID) {
                partialFillOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
                onPartialFill(partialFillOrderDecoder);
            } else if (header.templateId() == FillOrderDecoder.TEMPLATE_ID) {
                fillOrderDecoder.wrap(buffer, bufferOffset, actingBlockLength, actingVersion);
                onFill(fillOrderDecoder);
            }
        }
    }

    public abstract void onCreateOrder(final CreateOrderDecoder create);

    public abstract void onCancelOrder(final CancelOrderDecoder cancel);

    public abstract void onAckedOrder(final AckedOrderDecoder acked);

    public abstract void onCancelAckedOrder(final CancelAckedOrderDecoder cancelAcked);

    public abstract void onPendingOrder(final PendingOrderDecoder pending);

    public abstract void onPartialFill(final PartialFillOrderDecoder partialFill);

    public abstract void onFill(final FillOrderDecoder fill);
}

package codingblackfemales.orderbook.channel;

import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.sequencer.Sequencer;
import messages.order.FillOrderEncoder;
import messages.order.MessageHeaderEncoder;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class OrderChannel {

    private static final Logger logger = LoggerFactory.getLogger(OrderChannel.class);

    private final Sequencer sequencer;

    public OrderChannel(Sequencer sequencer) {
        this.sequencer = sequencer;
    }

    public void publishFill(final long fillQuantity, final long price, final LimitOrderFlyweight limit){

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();

        final FillOrderEncoder fillEncoder = new FillOrderEncoder();
        //write the encoded output to the direct buffer
        fillEncoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);


        fillEncoder.orderId(limit.getOrderId());
        fillEncoder.quantity(fillQuantity);
        fillEncoder.price(price);

        logger.info("[ORDERBOOK] publishing fill to stream: " + fillEncoder);

        this.sequencer.onCommand(directBuffer);
    }

}

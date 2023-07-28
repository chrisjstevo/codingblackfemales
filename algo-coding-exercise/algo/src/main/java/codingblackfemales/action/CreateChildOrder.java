package codingblackfemales.action;

import codingblackfemales.sequencer.Sequencer;
import messages.order.CreateOrderEncoder;
import messages.order.MessageHeaderEncoder;
import messages.order.Side;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class CreateChildOrder implements Action {

    private static final Logger logger = LoggerFactory.getLogger(CreateChildOrder.class);
    private final long quantity;
    private final long price;

    private final Side side;



    public CreateChildOrder(final Side side, final long quantity, final long price) {
        this.quantity = quantity;
        this.price = price;
        this.side = side;
    }

    @Override
    public String toString() {
        return "CreateChildOrder(side=" + side + ",quantity=" + quantity + ",price=" + price + ")";
    }

    @Override
    public void apply(Sequencer sequencer) {
        final CreateOrderEncoder encoder = new CreateOrderEncoder();
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();

        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        headerEncoder.schemaId(CreateOrderEncoder.SCHEMA_ID);
        headerEncoder.version(CreateOrderEncoder.SCHEMA_VERSION);
        encoder.price(price);
        encoder.quantity(quantity);
        encoder.side(side);
        sequencer.onCommand(directBuffer);
    }
}

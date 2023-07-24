package codingblackfemales.action;

import codingblackfemales.sequencer.Sequencer;
import messages.order.MessageHeaderEncoder;
import messages.order.CreateOrderEncoder;
import messages.order.Side;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class CreateChildOrder implements Action {
    private final long quantity;
    private final long price;

    private final Side side;

    private final CreateOrderEncoder encoder = new CreateOrderEncoder();
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    private final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();

    public CreateChildOrder(final Side side, final long quantity, final long price) {
        this.quantity = quantity;
        this.price = price;
        this.side = side;
    }

    @Override
    public void apply(Sequencer sequencer) throws Exception {
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);
        headerEncoder.schemaId(CreateOrderEncoder.SCHEMA_ID);
        headerEncoder.version(CreateOrderEncoder.SCHEMA_VERSION);
        encoder.price(price);
        encoder.quantity(quantity);
        encoder.side(side);
        sequencer.onCommand(directBuffer);
    }
}

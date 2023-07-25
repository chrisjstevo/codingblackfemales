package codingblackfemales.sequencer.net;

import org.agrona.DirectBuffer;

import java.util.LinkedList;
import java.util.List;

public class TestNetwork implements Network{

    private final List<Consumer> consumers = new LinkedList<>();

    @Override
    public void dispatch(DirectBuffer buffer){
        for (Consumer consumer: consumers) {
            consumer.onMessage(buffer);
        }
    }

    public void addConsumer(Consumer consumer){
        consumers.add(consumer);
    }

}

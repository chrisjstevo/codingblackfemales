package codingblackfemales.service;

import codingblackfemales.container.RunTrigger;
import codingblackfemales.sequencer.net.Consumer;
import org.agrona.DirectBuffer;

public class OrderService implements Consumer {

    private final RunTrigger runTrigger;

    public OrderService(RunTrigger runTrigger) {
        this.runTrigger = runTrigger;
    }

    @Override
    public void onMessage(DirectBuffer buffer) throws Exception {

    }
}

package codingblackfemales.gettingstarted;

import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.orderbook.OrderBook;
import codingblackfemales.orderbook.channel.MarketDataChannel;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.consumer.OrderBookInboundOrderConsumer;
import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.consumer.LoggingConsumer;
import codingblackfemales.sequencer.marketdata.SequencerTestCase;
import codingblackfemales.sequencer.net.TestNetwork;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import messages.marketdata.*;
import messages.order.Side;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class MeanReversionBackTest extends SequencerTestCase {

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder encoder = new BookUpdateEncoder();

    private AlgoContainer container;

    @Override
    public Sequencer getSequencer() {
        final TestNetwork network = new TestNetwork();
        final Sequencer sequencer = new DefaultSequencer(network);

        final RunTrigger runTrigger = new RunTrigger();
        final Actioner actioner = new Actioner(sequencer);

        final MarketDataChannel marketDataChannel = new MarketDataChannel(sequencer);
        final OrderChannel orderChannel = new OrderChannel(sequencer);
        final OrderBook book = new OrderBook(marketDataChannel, orderChannel);

        final OrderBookInboundOrderConsumer orderConsumer = new OrderBookInboundOrderConsumer(book);

        container = new AlgoContainer(new MarketDataService(runTrigger), new OrderService(runTrigger), runTrigger, actioner);
        //set my algo logic
        container.setLogic(new MeanReversion());

        network.addConsumer(new LoggingConsumer());
        network.addConsumer(book);
        network.addConsumer(container.getMarketDataService());
        network.addConsumer(container.getOrderService());
        network.addConsumer(orderConsumer);
        network.addConsumer(container);

        return sequencer;
    }

    private UnsafeBuffer createSampleMarketDataTick() {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        // Write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        // Set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);
        encoder.source(Source.STREAM);

        encoder.bidBookCount(3)
                .next().price(98L).size(100L)
                .next().price(95L).size(200L)
                .next().price(91L).size(300L);

        encoder.askBookCount(4)
                .next().price(100L).size(101L)
                .next().price(110L).size(200L)
                .next().price(115L).size(5000L)
                .next().price(119L).size(5600L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        return directBuffer;
    }

    private UnsafeBuffer createSampleMarketDataTick2() {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        // Write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        // Set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);
        encoder.source(Source.STREAM);

        encoder.bidBookCount(3)
                .next().price(95L).size(100L)
                .next().price(93L).size(200L)
                .next().price(91L).size(300L);

        encoder.askBookCount(4)
                .next().price(93L).size(101L)
                .next().price(94L).size(100L)
                .next().price(95).size(50L)
                .next().price(110L).size(56L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        return directBuffer;
    }

    private UnsafeBuffer createSampleMarketDataTick3() {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        // Write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        // Set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);
        encoder.source(Source.STREAM);

        encoder.bidBookCount(3)
                .next().price(120L).size(100L)
                .next().price(121L).size(200L)
                .next().price(125L).size(300L);

        encoder.askBookCount(5)
                .next().price(93L).size(1L)
                .next().price(94L).size(10L)
                .next().price(95).size(50L)
                .next().price(100L).size(56L)
                .next().price(110L).size(56L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        return directBuffer;
    }

    private UnsafeBuffer createSampleMarketDataTick4() {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        // Write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        // Set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);
        encoder.source(Source.STREAM);

        encoder.bidBookCount(3)
                .next().price(120L).size(100L)
                .next().price(121L).size(200L)
                .next().price(125L).size(300L);

        encoder.askBookCount(5)
                .next().price(93L).size(600L)
                .next().price(94L).size(10L)
                .next().price(95).size(50L)
                .next().price(100L).size(56L)
                .next().price(110L).size(56L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

        return directBuffer;
    }

    @Test
    public void createOrdersUsingAlgo() throws Exception {
        // Create a sample market data tick....
        send(createSampleMarketDataTick());

        // check we had 5 orders created
        assertEquals(container.getState().getChildOrders().size(), 5);
    }

    @Test
    public void fillOrdersUsingAlgo() throws Exception {
        // Create a sample market data tick....
        send(createSampleMarketDataTick());

        // When Market data moves towards us
        send(createSampleMarketDataTick2());

        // Get the state
        var state = container.getState();
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).orElse(0L);

        // Check that our algo state was updated to reflect our fills when the market data
        assertEquals(251, filledQuantity);
    }

    @Test
    public void cancelOrdersUsingAlgo() throws Exception {
        // Create a sample market data tick....
        send(createSampleMarketDataTick());

        // When Market data moves towards us
        send(createSampleMarketDataTick2());


        // Check if the algorithm canceled orders
        var canceledOrders = container.getState().getChildOrders().stream()
                .filter(order -> order.getState() == OrderState.CANCELLED)
                .toList();

        assertEquals(1, canceledOrders.size());
    }


    @Test
    public void createSellOrders() throws Exception {
        // Create a sample market data tick....
        send(createSampleMarketDataTick());

        // When Market data moves towards us
        send(createSampleMarketDataTick2());

        // When Market data moves towards us
        send(createSampleMarketDataTick3());

        // When Market data moves towards us
        send(createSampleMarketDataTick4());

        // Check if the algorithm creates sell orders
        var sellOrders = container.getState().getChildOrders().stream()
                .filter(order -> order.getSide() == Side.SELL)
                .toList();

        assertEquals(1, sellOrders.size());

    }
}
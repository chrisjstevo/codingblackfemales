package codingblackfemales.gettingstarted;

import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.orderbook.order.Order;
import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.consumer.LoggingConsumer;
import codingblackfemales.sequencer.marketdata.SequencerTestCase;
import codingblackfemales.sequencer.net.TestNetwork;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.marketdata.*;
import messages.order.Side;

import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public abstract class AbstractAlgoTest extends SequencerTestCase {


    protected AlgoContainer container;

    @Override
    public Sequencer getSequencer() {
        final TestNetwork network = new TestNetwork();
        final Sequencer sequencer = new DefaultSequencer(network);

        final RunTrigger runTrigger = new RunTrigger();
        final Actioner actioner = new Actioner(sequencer);

        container = new AlgoContainer(new MarketDataService(runTrigger), new OrderService(runTrigger), runTrigger, actioner);
        //set my algo logic
        container.setLogic(createAlgoLogic());

        network.addConsumer(new LoggingConsumer());
        network.addConsumer(container.getMarketDataService());
        network.addConsumer(container.getOrderService());
        network.addConsumer(container);

        return sequencer;
    }

    public abstract AlgoLogic createAlgoLogic();


    protected UnsafeBuffer createTick(){

        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);

        encoder.askBookCount(3)
                .next().price(100L).size(101L)
                .next().price(110L).size(200L)
                .next().price(115L).size(5000L);

        encoder.bidBookCount(3)
                .next().price(98L).size(100L)
                .next().price(95L).size(200L)
                .next().price(91L).size(300L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    protected UnsafeBuffer createMaxOrderTick( int maxAcceptedOrders){
      
    // private MarketData createMaxOrderTick(int maxAcceptedOrders) {
        // Create a sample market data tick that should trigger orders

        // MarketData marketData = new MarketData();
        // Set market data properties as needed to simulate the scenario
        final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        final BookUpdateEncoder encoder = new BookUpdateEncoder();

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        //set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);

        encoder.askBookCount(2)
                .next().price(110L).size(160L)
                .next().price(120L).size(220L);

        encoder.bidBookCount(2)
                .next().price(97L).size(95L)
                .next().price(94L).size(150L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        for (int i = 0; i < maxAcceptedOrders; i++) {
            // Create and add orders to the market data
            var state = container.getState();
            // BidLevel level = state.getBidAt(0);
            // final long price = level.price;
            // final long quantity = level.quantity;
            //  AskLevel farTouch = state.getAskAt(0);
            // long quantity = farTouch.quantity;
            // long price = farTouch.price;
            // final BidLevel nearTouch = state.getBidAt(0);

            //if values not hardcoded they dont seem to work

            long quantity = 2;
            // the above is the num orders
            long price = 110L;
            
            
            new CreateChildOrder(Side.BUY, quantity, price);
            // Order order = createOrder();
            // marketData.addOrder(order);
        }

        return directBuffer;
    }

    // protected UnsafeBuffer noActionTick(int ordersNotCanx) {
    //     // active orders less than 5
    //     // option is not present
    //     //  has less than number of children that were requested
    //     // Create a sample market data tick that should trigger no action
    //     final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    //     final BookUpdateEncoder encoder = new BookUpdateEncoder();

    //     final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    //     final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

    //     //write the encoded output to the direct buffer
    //     encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

    //     //set the fields to desired values
    //     encoder.venue(Venue.XLON);
    //     encoder.instrumentId(123L);

    //     encoder.askBookCount(3)
    //             .next().price(100L).size(101L)
    //             .next().price(110L).size(200L)
    //             .next().price(115L).size(5000L);

    //     encoder.bidBookCount(3)
    //             .next().price(98L).size(100L)
    //             .next().price(95L).size(200L)
    //             .next().price(91L).size(300L);

    //     encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
    //     encoder.source(Source.STREAM);

    //     for (int i = 0; i < ordersNotCanx; i++) {
    //         // Create and add orders to the market data
    //         var state = container.getState();
    //         // BidLevel level = state.getBidAt(0);
    //         // final long price = level.price;
    //         // final long quantity = level.quantity;
    //         //  AskLevel farTouch = state.getAskAt(0);
    //         // long quantity = farTouch.quantity;
    //         // long price = farTouch.price;
    //         // final BidLevel nearTouch = state.getBidAt(0);

    //         //if values not hardcoded they dont seem to work

    //         long quantity = 2;
    //         long price = 110L;
            
            
    //         new CreateChildOrder(Side.BUY, quantity, price);

    //     return directBuffer;
    // }

        // protected UnsafeBuffer createActiveOrderTick(int activeOrderCount) {
        // // Create a sample market data tick with the specified number of active orders
        // // MarketData marketData = new MarketData();
        // // Set market data properties as needed
        // final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
        // final BookUpdateEncoder encoder = new BookUpdateEncoder();

        // final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        // final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        // //write the encoded output to the direct buffer
        // encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        // //set the fields to desired values
        // encoder.venue(Venue.XLON);
        // encoder.instrumentId(123L);

        // encoder.askBookCount(3)
        //         .next().price(100L).size(101L)
        //         .next().price(110L).size(200L)
        //         .next().price(115L).size(5000L);

        // encoder.bidBookCount(3)
        //         .next().price(98L).size(100L)
        //         .next().price(95L).size(200L)
        //         .next().price(91L).size(300L);

        // encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        // encoder.source(Source.STREAM);

        // // Create and add active orders to the market data
        // for (int i = 0; i < activeOrderCount; i++) {
        //     Order order = createActiveOrder();
        //     // marketData.addOrder(order);
        // }
        // return directBuffer;
    // }

}

package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.consumer.LoggingConsumer;
import codingblackfemales.sequencer.marketdata.SequencerTestCase;
import codingblackfemales.sequencer.net.TestNetwork;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

// AbstractAlgoTest provides the common test setup/infrastructure
// AbstractAlgoTest is a class/template that provides the test structure that MyAlgoTest and MyAlgoBackTest inherit
public abstract class AbstractAlgoTest extends SequencerTestCase {

        protected AlgoContainer container;

        @Override
        public Sequencer getSequencer() {
                final TestNetwork network = new TestNetwork();
                final Sequencer sequencer = new DefaultSequencer(network);

                final RunTrigger runTrigger = new RunTrigger();
                final Actioner actioner = new Actioner(sequencer);
                // main container for algo
                container = new AlgoContainer(new MarketDataService(runTrigger), new OrderService(runTrigger),
                                runTrigger,
                                actioner);
                // set my algo logic
                container.setLogic(createAlgoLogic());

                // Connect the logging, market data, order and algo components
                // to the test network so they can receive and process messages.
                network.addConsumer(new LoggingConsumer());
                network.addConsumer(container.getMarketDataService());
                network.addConsumer(container.getOrderService());
                network.addConsumer(container);

                return sequencer;
        }

        public abstract AlgoLogic createAlgoLogic();

        // a tick is a snapshot/update of the market data at a particular point in time
        // createTick() method creates a piece of market data that tells algo what the
        // order book currently looks like

        protected UnsafeBuffer createTick() {

                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                // write the encoded output to the direct buffer
                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                // set the fields to desired values
                encoder.venue(Venue.LME);
                encoder.instrumentId(123L);

                // Bid level — a price level where buyers are willing to buy.
                // Ask level — a price level where sellers are willing to sell.
                // Price level — a particular price and the quantity available at that price.

                // creates the BID side of the order book
                // 3 bid price levels
                encoder.bidBookCount(3)
                                .next().price(98L).size(100L)
                                .next().price(95L).size(200L)
                                .next().price(91L).size(300L);

                // creates the ASK side of the order book
                // 3 ask price levels
                encoder.askBookCount(3)
                                .next().price(100L).size(101L)
                                .next().price(110L).size(200L)
                                .next().price(115L).size(5000L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
                encoder.source(Source.STREAM);

                return directBuffer;
        }

        protected UnsafeBuffer createTick2() {

                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                encoder.venue(Venue.LME);
                encoder.instrumentId(123L);

                // Best bid has moved from 98 to 100
                encoder.bidBookCount(2)
                                .next().price(100L).size(100L)
                                .next().price(98L).size(200L);

                encoder.askBookCount(2)
                                .next().price(102L).size(500L)
                                .next().price(105L).size(200L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
                encoder.source(Source.STREAM);

                return directBuffer;
        }

        protected UnsafeBuffer createTick3() {

                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                encoder.venue(Venue.LME);
                encoder.instrumentId(123L);

                // Best bid is still 98, so an existing BUY order should remain valid
                encoder.bidBookCount(2)
                                .next().price(98L).size(150L)
                                .next().price(95L).size(200L);

                encoder.askBookCount(2)
                                .next().price(100L).size(500L)
                                .next().price(105L).size(200L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
                encoder.source(Source.STREAM);

                return directBuffer;
        }

        protected UnsafeBuffer createTick4() {

                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                encoder.venue(Venue.LME);
                encoder.instrumentId(123L);

                // Best bid is above maximum buying price of 98
                // algo should not create an order
                encoder.bidBookCount(2)
                                .next().price(100L).size(150L)
                                .next().price(98L).size(200L);

                encoder.askBookCount(2)
                                .next().price(102L).size(500L)
                                .next().price(105L).size(200L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
                encoder.source(Source.STREAM);

                return directBuffer;
        }

}

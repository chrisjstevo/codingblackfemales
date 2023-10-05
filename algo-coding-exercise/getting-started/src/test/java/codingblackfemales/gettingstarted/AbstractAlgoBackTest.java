package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
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
import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public abstract class AbstractAlgoBackTest extends SequencerTestCase {

        protected AlgoContainer container;

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

                container = new AlgoContainer(new MarketDataService(runTrigger), new OrderService(runTrigger),
                                runTrigger, actioner);
                // set my algo logic
                container.setLogic(createAlgoLogic());

                network.addConsumer(new LoggingConsumer());
                network.addConsumer(book);
                network.addConsumer(container.getMarketDataService());
                network.addConsumer(container.getOrderService());
                network.addConsumer(orderConsumer);
                network.addConsumer(container);

                return sequencer;
        }

        public abstract AlgoLogic createAlgoLogic();

        protected UnsafeBuffer createTick() {
                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                // write the encoded output to the direct buffer
                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                // set the fields to desired values
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

        protected UnsafeBuffer createTick2() {

                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                // write the encoded output to the direct buffer
                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                // set the fields to desired values
                encoder.venue(Venue.XLON);
                encoder.instrumentId(123L);
                encoder.source(Source.STREAM);

                encoder.bidBookCount(3)
                                .next().price(95L).size(100L)
                                .next().price(93L).size(200L)
                                .next().price(91L).size(300L);

                encoder.askBookCount(4)
                                .next().price(98L).size(501L)
                                .next().price(101L).size(200L)
                                .next().price(110L).size(5000L)
                                .next().price(119L).size(5600L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

                return directBuffer;
        }

        protected UnsafeBuffer createTick3() {

                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                encoder.venue(Venue.XLON);
                encoder.instrumentId(123L);
                encoder.source(Source.STREAM);

                // Modify bid book and ask book prices and sizes as desired
                encoder.bidBookCount(3)
                                .next().price(92L).size(100L)
                                .next().price(90L).size(200L)
                                .next().price(91L).size(300L);

                encoder.askBookCount(4)
                                .next().price(97L).size(501L)
                                .next().price(111L).size(200L)
                                .next().price(110L).size(5000L)
                                .next().price(119L).size(5600L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

                return directBuffer;
        }

        protected UnsafeBuffer createTick4() {
                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                // Write the encoded output to the direct buffer
                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                // Set the fields to desired values
                encoder.venue(Venue.XLON);
                encoder.instrumentId(123L);
                encoder.source(Source.STREAM);

                // Modify bid book and ask book prices and sizes as desired
                encoder.bidBookCount(3)
                                .next().price(99L).size(150L)  
                                .next().price(96L).size(250L)  
                                .next().price(92L).size(350L); 

                encoder.askBookCount(4)
                                .next().price(101L).size(110L) 
                                .next().price(111L).size(220L) 
                                .next().price(116L).size(5100L) 
                                .next().price(120L).size(5700L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

                return directBuffer;
        }

        protected UnsafeBuffer createTick5() {
                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                encoder.venue(Venue.XLON);
                encoder.instrumentId(123L);
                encoder.source(Source.STREAM);

                // Modify bid book and ask book prices and sizes as desired
                encoder.bidBookCount(3)
                                .next().price(97L).size(130L) 
                                .next().price(94L).size(240L) 
                                .next().price(90L).size(320L); 

                encoder.askBookCount(4)
                                .next().price(102L).size(120L) 
                                .next().price(112L).size(210L) 
                                .next().price(117L).size(5100L) 
                                .next().price(121L).size(5800L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

                return directBuffer;
        }

        protected UnsafeBuffer createTickUpTrend() {
                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                encoder.venue(Venue.XLON);
                encoder.instrumentId(123L);
                encoder.source(Source.STREAM);

                // Modify bid book and ask book prices and sizes as desired
                encoder.bidBookCount(3)
                                .next().price(200L).size(90L)
                                .next().price(210L).size(200L)
                                .next().price(220L).size(300L);

                encoder.askBookCount(3)
                                .next().price(230L).size(501L)
                                .next().price(240L).size(200L)
                                .next().price(250L).size(5000L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

                return directBuffer;
        }

        protected UnsafeBuffer createTickDownTrend() {
                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                encoder.venue(Venue.XLON);
                encoder.instrumentId(123L);
                encoder.source(Source.STREAM);

                // Modify bid book and ask book prices and sizes as desired
                encoder.bidBookCount(3)
                                .next().price(100L).size(100L)
                                .next().price(99L).size(200L)
                                .next().price(98L).size(300L);

                encoder.askBookCount(4)
                                .next().price(98L).size(101L)
                                .next().price(97L).size(200L)
                                .next().price(96L).size(5000L)
                                .next().price(95L).size(5600L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

                return directBuffer;
        }

        protected UnsafeBuffer createTickStable() {
                final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
                final BookUpdateEncoder encoder = new BookUpdateEncoder();

                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);


                encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

                encoder.venue(Venue.XLON);
                encoder.instrumentId(123L);
                encoder.source(Source.STREAM);

                // Modify bid book and ask book prices and sizes as desired
                encoder.bidBookCount(3)
                                .next().price(98L).size(100L)
                                .next().price(99L).size(200L)
                                .next().price(98L).size(300L);

                encoder.askBookCount(4)
                                .next().price(98L).size(101L)
                                .next().price(98L).size(200L)
                                .next().price(98L).size(5000L)
                                .next().price(98L).size(5600L);

                encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);

                return directBuffer;
        }

}

package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.marketdata.api.MarketDataMessage;
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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractMovingAverageBackTest extends SequencerTestCase {

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder encoder = new BookUpdateEncoder();

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

        container = new AlgoContainer(new MarketDataService(runTrigger), new OrderService(runTrigger), runTrigger,
                actioner);
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

    /**
     * @param numTicks number of Ticks I want to generate
     * @return an array of UnsafeBuffer[] representing the ticks
     */
    protected UnsafeBuffer[] createSampleMarketDataTick(int numTicks) {
        UnsafeBuffer[] marketDataTicks = new UnsafeBuffer[numTicks];



        for (int i = 0; i < numTicks; i++) {

            final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024 * numTicks);
            final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

            encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

            encoder.venue(Venue.XLON);
            encoder.instrumentId(123L);
            encoder.source(Source.STREAM);

            int bidBookCount = 3;
            int askBookCount = 4;

            var bidEncoder = encoder.bidBookCount(bidBookCount);
            for (int j = 0; j < bidBookCount; j++) {
                long price = getRandomLongWithinRange(90, 189);
                long size = getRandomLongWithinRange(100, 1099);

                bidEncoder.next().price(price).size(size);
            }

            var askEncoder = encoder.askBookCount(askBookCount);
            for (int j = 0; j < askBookCount; j++) {
                long price = getRandomLongWithinRange(100, 199);
                long size = getRandomLongWithinRange(100, 1099);
                if(price < 100|| size < 100){
                    price = getRandomLongWithinRange(100, 199);
                    size = getRandomLongWithinRange(100, 1099);
                    askEncoder.next().price(price).size(size);
                }
                askEncoder.next().price(price).size(size);
            }

            encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
            marketDataTicks[i] = directBuffer;
        }
        return marketDataTicks;
    }

    private  static int getRandomLongWithinRange(int min, int max){

        return ThreadLocalRandom.current().nextInt(min, max +1);
    }

}

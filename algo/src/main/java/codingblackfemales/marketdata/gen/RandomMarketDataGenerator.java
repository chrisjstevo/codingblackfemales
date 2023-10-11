package codingblackfemales.marketdata.gen;

import codingblackfemales.marketdata.api.BookEntry;
import codingblackfemales.marketdata.api.MarketDataMessage;
import codingblackfemales.marketdata.impl.AskBookUpdateImpl;
import codingblackfemales.marketdata.impl.BidBookUpdateImpl;
import codingblackfemales.marketdata.impl.BookUpdateImpl;
import messages.marketdata.InstrumentStatus;
import messages.marketdata.Venue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

public class RandomMarketDataGenerator implements MarketDataGenerator {
    private static final long BID_START = Long.MIN_VALUE;
    private static final long ASK_START = Long.MAX_VALUE;
    private static final int spreadMultiplierMin = 5;
    private static final int spreadMultiplierMax = 12;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final long startPriceLevel;
    private long spreadMultiplier;
    private final long priceMaxDelta;
    private final long instrumentId;
    private final Venue venue;
    private long marketDataMessagesMaxLevel;
    private final InstrumentStatus instrumentStatus = InstrumentStatus.CONTINUOUS;
    private final Comparator<Order> buysCompare = Order::compareTo;
    private final Queue<Order> buys = new PriorityQueue<>(buysCompare);
    private final Comparator<Order> sellsCompare = (t1, t2) -> -1 * t1.compareTo(t2);
    private final Queue<Order> sells = new PriorityQueue<>(sellsCompare);
    private long mid;
    private long bid = BID_START;
    private long ask = ASK_START;

    public RandomMarketDataGenerator(final long instrumentId,
                                     final Venue venue,
                                     final long priceLevel,
                                     final long priceMaxDelta,
                                     final long marketDataMessagesMaxLevel) {
        this.startPriceLevel = this.mid = priceLevel;
        this.priceMaxDelta = priceMaxDelta;
        this.instrumentId = instrumentId;
        this.venue = venue;
        this.marketDataMessagesMaxLevel = marketDataMessagesMaxLevel;
        initBook();
    }

    @Override
    public MarketDataMessage next() {
        return updateBook();
    }

    public void initBook() {
        bid = rand(mid - priceMaxDelta, mid - 1);
        ask = rand(mid + 1, mid + priceMaxDelta);

        for (int i = 0; i < rand0Max(10); i++) {
            Order order = buyTrade();
            addBuy(order);
        }
        for (int i = 0; i < rand0Max(10); i++) {
            Order order = sellTrade();
            addSell(order);
        }
    }

    private void addSell(Order order) {
        sells.add(order);
        updateAsk(order);
    }

    private void addBuy(Order order) {
        buys.add(order);
        updateBid(order);
    }

    enum Action {
        Cancel,
        UpdateQty,
        UpdatePrice,
        NewOrder
    }

    enum Side {
        Buy,
        Sell
    }

    public MarketDataMessage updateBook() {
        int updateCount = toIntExact(rand(1, 3));
        boolean buyUpdated = false;
        boolean sellUpdated = false;
        for (int i = 0; i < updateCount; i++) {
            Side updated = doUpdateBook();
            if (updated == Side.Buy) {
                buyUpdated = true;
            } else if (updated == Side.Sell) {
                sellUpdated = true;
            }
        }

        if (!sellUpdated && !buyUpdated) {
            // we have been very unlucky, just insert new order
            Side side = Side.values()[(int) rand0Max(Side.values().length)];
            Queue<Order> orders = side == Side.Buy ? buys : sells;
            newOrder(side, orders);
            if (side == Side.Buy) {
                buyUpdated = true;
            } else {
                sellUpdated = true;
            }
        }

        return toMarketDataMessage(buyUpdated, sellUpdated);
    }

    private Side doUpdateBook() {
        for (int i = 0; i < 20; i++) {
            Side side = Side.values()[(int) rand0Max(Side.values().length)];
            Queue<Order> orders = side == Side.Buy ? buys : sells;
            Action action = Action.values()[(int) rand0Max(Action.values().length)];

            switch (action) {
                case Cancel:
                    if (orders.size() == 0) continue; // can't perform update for: (side & action) try again
                    cancel(side, orders);
                    break;
                case UpdateQty:
                    if (orders.size() == 0) continue; // can't perform update for: (side & action) try again
                    updateQty(side, orders);
                    break;
                case UpdatePrice:
                    if (orders.size() == 0) continue; // can't perform update for: (side & action) try again
                    updatePrice(side, orders);
                    break;
                case NewOrder:
                    newOrder(side, orders);
                    break;
            }
            return side;
        }
        return null;
    }

    private void cancel(Side side, Queue<Order> orders) {
        if (orders.size() > 0) {
            int idx = toIntExact(rand0Max(orders.size()));
            orders.remove(orders.toArray()[idx]);
            logger.debug("cancel side=[{}] idx=[{}] qty=[{}]", side, idx);
            updateAskBid();
        }
    }

    private void updateQty(Side side, Queue<Order> orders) {
        if (orders.size() > 0) {
            int idx = toIntExact(rand0Max(orders.size()));
            Order order = (Order) orders.toArray()[idx];
            order.qty = nextQty();
            logger.debug("qty_update side=[{}] idx=[{}] qty=[{}]", side, idx, order.qty);
        }
    }

    private void updatePrice(Side side, Queue<Order> orders) {
        if (orders.size() > 0) {
            nextSpreadMultiplier();
            int idx = toIntExact(rand0Max(orders.size()));
            Order order = (Order) orders.toArray()[idx];
            orders.remove(order);
            order.price = side == Side.Buy ? nextBid() : nextAsk();
            orders.add(order);
            updateAskBid();
            logger.debug("price_update side=[{}] idx=[{}] price=[{}]", side, idx, order.price);
        }
    }

    private void newOrder(Side side, Queue<Order> orders) {
        nextSpreadMultiplier();
        final Order order;
        if (side == Side.Buy) {
            addBuy(order = new Order(nextBid(), nextQty()));
        } else {
            addSell(order = new Order(nextAsk(), nextQty()));
        }
        logger.debug("new_trade side=[{}] trade=[{}]", side, order);
    }

    private void nextSpreadMultiplier() {
        this.spreadMultiplier = rand(spreadMultiplierMin, spreadMultiplierMax);
    }

    private void updateBid(Order order) {
        this.bid = Math.max(order.price, bid);
        updatePriceTarget();
    }

    private void updateAsk(Order order) {
        this.ask = Math.min(order.price, ask);
        updatePriceTarget();
    }

    private void updateAskBid() {
        if (buys.size() == 0 && sells.size() == 0) {
            this.mid = startPriceLevel;
            this.ask = ASK_START;
            this.bid = BID_START;
        } else if (buys.size() != 0 && sells.size() == 0) {
            this.mid = buys.peek().price;
            this.ask = ASK_START;
            this.bid = buys.peek().price;
        } else if (buys.size() == 0 && sells.size() != 0) {
            this.mid = sells.peek().price;
            this.ask = sells.peek().price;
            this.bid = BID_START;
        } else {
            this.mid = (buys.peek().price + sells.peek().price) / 2;
            this.ask = sells.peek().price;
            this.bid = buys.peek().price;
        }
    }

    private void updatePriceTarget() {
        if (ask != ASK_START && bid != BID_START) {
            mid = (ask + bid) / 2;
        }
    }

    Order buyTrade() {
        long bid = nextBid();
        return new Order(bid, nextQty());
    }

    private long nextBid() {
        long minBid = minBid();
        long maxBid = maxBid();
        return rand(minBid, maxBid);
    }

    private long maxBid() {
        return ask != ASK_START ? Math.min(ask - 1, mid) : mid;
    }

    private long minBid() {
        return bid != BID_START ? Math.max(bid - Math.min(spreadMultiplier * spread(), 10), mid - priceMaxDelta) : mid - priceMaxDelta;
    }

    Order sellTrade() {
        long ask = nextAsk();
        return new Order(ask, nextQty());
    }

    private long nextAsk() {
        long minAsk = minAsk();
        long maxAsk = maxAsk();
        return rand(minAsk, maxAsk);
    }

    private long maxAsk() {
        return ask != ASK_START ? Math.min(ask + spreadMultiplier * spread(), mid + priceMaxDelta) : (mid + priceMaxDelta);
    }

    private long minAsk() {
        return Math.max(bid + 1, mid);
    }

    private long nextQty() {
        return rand(1, 100);
    }

    private long spread() {
        return Math.min(Math.abs(bid - ask), 100);
    }

    private long rand0Max(long bound) {
        return ThreadLocalRandom.current().nextLong(bound);
    }

    private long rand(long min, long max) {
        if (min >= max) {
            throw new RuntimeException(String.format("min >= max -- min=[%s] max=[%s]", min, max));
        }
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    class Order implements Comparable<Order> {
        long price;
        long qty;

        public Order(long price, long qty) {
            this.price = price;
            this.qty = qty;
        }

        public long getPrice() {
            return price;
        }

        public long getQty() {
            return qty;
        }

        @Override
        public int compareTo(Order o) {
            return -1 * Long.compare(price, o.price);
        }

        @Override
        public String toString() {
            return "Trade{" +
                    "price=" + price +
                    ", qty=" + qty +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RandomMarketDataGenerator{" +
                String.format("\n mid=%d spraed=%d spreadMultiplier=%d", mid, spread(), spreadMultiplier) +
                String.format("\n bid=%d min/max=(%d/%d)", bid, minBid(), maxBid()) +
                String.format("\n ask=%d min/max=(%d/%d)", ask, minAsk(), maxAsk()) +
                "\n buys=\n\t" + buys.stream().sorted(buysCompare).map(Object::toString).collect(Collectors.joining("\n\t")) +
                "\n sells=\n\t" + sells.stream().sorted(sellsCompare).map(Object::toString).collect(Collectors.joining("\n\t")) +
                '}';
    }

    private MarketDataMessage toMarketDataMessage(final boolean buyUpdated,
                                                  final boolean sellUpdated) {
        if (buyUpdated && sellUpdated) {
            return toBookUpdate();
        } else if (buyUpdated) {
            return toBidBookUpdate();
        } else if (sellUpdated) {
            return toAskBookUpdate();
        } else {
            throw new IllegalStateException();
        }
    }

    private MarketDataMessage toAskBookUpdate() {
        return new AskBookUpdateImpl(instrumentId, venue, toBookEntries(this.sells));
    }

    private MarketDataMessage toBidBookUpdate() {
        return new BidBookUpdateImpl(instrumentId, venue, toBookEntries(this.buys));
    }

    private MarketDataMessage toBookUpdate() {
        return new BookUpdateImpl(instrumentId, venue, instrumentStatus, toBookEntries(this.buys), toBookEntries(this.sells));
    }

    private List<BookEntry> toBookEntries(Queue<Order> orders) {
        List<BookEntry> bidBook = new ArrayList<>();
        Queue<Order> buys = new PriorityQueue(orders);
        Order buy;
        int count = 0;
        while ((buy = buys.poll()) != null) {
            bidBook.add(new BookEntry().setPrice(buy.price).setSize(buy.qty));
            count++;
            if (count == marketDataMessagesMaxLevel) {
                break;
            }
        }
        return bidBook;
    }
}
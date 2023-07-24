package codingblackfemales.orderbook;

import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import codingblackfemales.orderbook.visitor.MutatingRemoveOneOrderVisitor;
import messages.order.Side;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OrderBookVisitorTest {

    @Test
    public void testMarketDataAddRemovalVisitor(){

        final AskBookSide side = new AskBookSide();
        side.addMarketDataOrder(new MarketDataOrderFlyweight(1000, 10_500));
        side.addMarketDataOrder(new MarketDataOrderFlyweight(1100, 21_000));
        side.addMarketDataOrder(new MarketDataOrderFlyweight(1200, 35_000));

        assertEquals(1000L, side.getFirstLevel().getPrice());
        assertEquals(1100L, side.getFirstLevel().next().getPrice());
        assertEquals(1200L, side.getFirstLevel().next().next().getPrice());

        assertEquals(10_500L, side.getFirstLevel().getQuantity());
        assertEquals(21_000L, side.getFirstLevel().next().getQuantity());
        assertEquals(35_000L, side.getFirstLevel().next().next().getQuantity());

        side.removeMarketDataOrders();

        assertNull(side.getFirstLevel());

        side.addMarketDataOrder(new MarketDataOrderFlyweight(1000, 11_500));

        assertEquals(1000L, side.getFirstLevel().getPrice());
        assertEquals(11_500L, side.getFirstLevel().getQuantity());
        assertNull(side.getFirstLevel().next());
    }

    @Test
    public void testOneLimitOrderRemovalVisitorAskBook(){

        final AskBookSide side = new AskBookSide();

        side.addLimitOrder(new LimitOrderFlyweight(Side.SELL, 1200L, 10_123, 123456));

        assertEquals(1200L, side.getFirstLevel().getPrice());
        assertEquals(10_123L, side.getFirstLevel().getQuantity());
        assertNull(side.getFirstLevel().getFirstOrder().next());

        side.addLimitOrder(new LimitOrderFlyweight(Side.SELL,1300L, 50_000, 123457));
        side.addLimitOrder(new LimitOrderFlyweight(Side.SELL,1400L, 60_000, 123458));

        assertNull(side.getFirstLevel().getFirstOrder().next());

        side.addLimitOrder(new LimitOrderFlyweight(Side.SELL,1200L, 20_000, 123459));

        assertEquals(1200L, side.getFirstLevel().getPrice());
        assertEquals(30_123L, side.getFirstLevel().getQuantity());

        assertEquals(123459L,((LimitOrderFlyweight)side.getFirstLevel().getFirstOrder().next()).getOrderId());

        MutatingRemoveOneOrderVisitor removeVisitor = new MutatingRemoveOneOrderVisitor();
        removeVisitor.setOrderIdToRemove(123459);

        side.accept(removeVisitor);

        assertEquals(1200L, side.getFirstLevel().getPrice());
        assertEquals(10_123L, side.getFirstLevel().getQuantity());

        assertNull(((LimitOrderFlyweight)side.getFirstLevel().getFirstOrder().next()));

    }

    @Test
    public void testOneLimitOrderRemovalVisitorBidBook(){

        final BidBookSide side = new BidBookSide();

        side.addLimitOrder(new LimitOrderFlyweight(Side.BUY,900L, 10_123, 123456));

        assertEquals(900L, side.getFirstLevel().getPrice());
        assertEquals(10_123L, side.getFirstLevel().getQuantity());
        assertNull(side.getFirstLevel().getFirstOrder().next());

        side.addLimitOrder(new LimitOrderFlyweight(Side.BUY, 800L, 50_000, 123457));
        side.addLimitOrder(new LimitOrderFlyweight(Side.BUY, 700L, 60_000, 123458));

        assertNull(side.getFirstLevel().getFirstOrder().next());

        side.addLimitOrder(new LimitOrderFlyweight(Side.BUY, 800L, 20_000, 123459));

        assertEquals(900L, side.getFirstLevel().getPrice());
        assertEquals(10_123L, side.getFirstLevel().getQuantity());

        assertEquals(70_000L,side.getFirstLevel().next().getQuantity());
        assertEquals(123459L,((LimitOrderFlyweight)side.getFirstLevel().next().getFirstOrder().next()).getOrderId());

        MutatingRemoveOneOrderVisitor removeVisitor = new MutatingRemoveOneOrderVisitor();
        removeVisitor.setOrderIdToRemove(123459);

        side.accept(removeVisitor);

        assertEquals(50_000L,side.getFirstLevel().next().getQuantity());

    }

}

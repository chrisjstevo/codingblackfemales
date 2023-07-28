package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import codingblackfemales.orderbook.order.MarketDataOrderFlyweight;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutatingMatchOneMarketDataOrderVisitor implements OrderBookVisitor{

    private static final Logger logger = LoggerFactory.getLogger(MutatingMatchOneMarketDataOrderVisitor.class);

    private final MarketDataOrderFlyweight orderToMatch;
    private long filledQuantity;
    private long remainingQuantity;
    private boolean isFullyFilled = false;

    private final OrderChannel orderChannel;

    public MutatingMatchOneMarketDataOrderVisitor(final MarketDataOrderFlyweight orderToMatch, final OrderChannel orderChannel) {
        this.filledQuantity = 0;
        this.orderToMatch = orderToMatch;
        this.remainingQuantity = orderToMatch.getQuantity();
        this.orderChannel = orderChannel;
    }

    @Override
    public void visitSide(OrderBookSide side) {}

    @Override
    public void visitLevel(OrderBookSide side, OrderBookLevel level) {
        logger.info("[ORDERBOOK] visiting Level" + level);
    }

    @Override
    public void visitOrder(DefaultOrderFlyweight order, OrderBookSide side, OrderBookLevel level, boolean isLast) {
        if(canMatchOrder(order)){
            logger.info("[ORDERBOOK] Have found order we can match:" + order + "(" + orderToMatch + ")");
            //if we can take all the order...
            if(remainingQuantity >= order.getQuantity()){
                long fillQuantity = order.getQuantity();
                remainingQuantity -= fillQuantity;
                filledQuantity += fillQuantity;
                level.setFirstOrder(order.remove());
                if(order instanceof LimitOrderFlyweight){
                    logger.info("Filled:" + fillQuantity + "@" + orderToMatch.getPrice());
                    publishFill(fillQuantity, orderToMatch.getPrice(),(LimitOrderFlyweight) order);
                }
            //if we can only take a nibble...
            }else if(remainingQuantity < order.getQuantity()){
                long fillQuantity = remainingQuantity;
                long remainingQty = order.getQuantity() - fillQuantity;
                remainingQuantity -= fillQuantity;
                filledQuantity += fillQuantity;
                order.setQuantity(remainingQty);
                if(order instanceof LimitOrderFlyweight){
                    logger.info("Filled:" + fillQuantity + "@" + orderToMatch.getPrice());
                    publishFill(fillQuantity, orderToMatch.getPrice(), (LimitOrderFlyweight) order);
                }
            }
        }else{
            logger.info("[ORDERBOOK] Can't match order:" + order + "(" + orderToMatch + ")");
        }
    }

    private boolean canMatchOrder(final DefaultOrderFlyweight order){
        return priceIsEqualOrMoreAggressive(order, this.orderToMatch);
    }

    private void publishFill(final long quantity, final long price, LimitOrderFlyweight orderFlyweight){
        logger.info("[ORDERBOOK] Filled " + quantity + "@" + price + " for order:" + orderFlyweight);
        orderChannel.publishFill(quantity, price, orderFlyweight);
    }

    private boolean priceIsEqualOrMoreAggressive(final DefaultOrderFlyweight bookOrder, final DefaultOrderFlyweight orderToMatch){
        boolean isEqOrMoreAgg = false;
        if(orderToMatch.getSide().equals(Side.BUY)){
            if(orderToMatch.getPrice() >= bookOrder.getPrice()){
                isEqOrMoreAgg = true;
            }
        }else if(orderToMatch.getSide().equals(Side.SELL)){
            if(orderToMatch.getPrice() <= bookOrder.getPrice()){
                isEqOrMoreAgg = true;
            }
        }
        return isEqOrMoreAgg;
    }

    @Override
    public OrderBookLevel missingBookLevel(OrderBookLevel previous, OrderBookLevel next, long price) {
        return null;
    }

    @Override
    public OrderBookLevel onNoFirstLevel() {
        return null;
    }

    @Override
    public DefaultOrderFlyweight onNoFirstOrder() {
        return null;
    }
}

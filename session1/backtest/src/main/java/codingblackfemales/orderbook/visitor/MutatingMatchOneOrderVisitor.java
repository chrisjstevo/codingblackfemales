package codingblackfemales.orderbook.visitor;

import codingblackfemales.orderbook.OrderBookLevel;
import codingblackfemales.orderbook.OrderBookSide;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.order.DefaultOrderFlyweight;
import codingblackfemales.orderbook.order.LimitOrderFlyweight;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutatingMatchOneOrderVisitor implements OrderBookVisitor{

    private static final Logger logger = LoggerFactory.getLogger(MutatingMatchOneOrderVisitor.class);

    private final LimitOrderFlyweight orderToMatch;
    private long filledQuantity;
    private long remainingQuantity;
    private boolean isFullyFilled = false;

    private final OrderChannel orderChannel;

    public MutatingMatchOneOrderVisitor(final LimitOrderFlyweight orderToMatch, final OrderChannel orderChannel) {
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
                level.setQuantity(level.getQuantity() - fillQuantity);
                if(level.getQuantity()==0){
                    side.setFirstLevel(level.remove());
                }
                publishFill(fillQuantity, order.getPrice(), orderToMatch);
            //if we can only take a nibble...
            }else if(remainingQuantity < order.getQuantity()){
                long fillQuantity = remainingQuantity;
                long remainingQty = order.getQuantity() - fillQuantity;
                remainingQuantity -= fillQuantity;
                filledQuantity += fillQuantity;
                order.setQuantity(remainingQty);
                level.setQuantity(level.getQuantity() - fillQuantity);
                if(level.getQuantity()==0){
                    side.setFirstLevel(level.remove());
                }
                publishFill(fillQuantity, order.getPrice(), orderToMatch);
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

    private boolean priceIsEqualOrMoreAggressive(final DefaultOrderFlyweight bookOrder, final LimitOrderFlyweight orderToMatch){
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

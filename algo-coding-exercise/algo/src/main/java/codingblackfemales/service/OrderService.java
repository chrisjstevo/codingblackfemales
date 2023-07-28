package codingblackfemales.service;

import codingblackfemales.container.RunTrigger;
import codingblackfemales.sequencer.event.OrderEventListener;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import messages.order.*;

import java.util.LinkedList;
import java.util.List;

public class OrderService extends OrderEventListener {

    private final RunTrigger runTrigger;

    private List<ChildOrder> children = new LinkedList<>();

    public OrderService(RunTrigger runTrigger) {
        this.runTrigger = runTrigger;
    }


    private void triggerRun(){
        runTrigger.triggerRun();
    }

    private ChildOrder createChildOrder(final CreateOrderDecoder create){
        return new ChildOrder(create.side(), create.orderId(), create.quantity(), create.price(), OrderState.PENDING);
    }

    private void updateState(ChildOrder child, int state){
        child.setState(state);
    }

    private void addChildFill(ChildOrder child, long filledQuantity, long filledPrice){
        child.addFill(filledQuantity, filledPrice);
    }

    @Override
    public void onCreateOrder(final CreateOrderDecoder create) {
        children.add(createChildOrder(create));
        triggerRun();
    }

    private ChildOrder find(long orderId){
        return children.stream().filter( order -> order.getOrderId() == orderId).findFirst().get();
    }

    @Override
    public void onCancelOrder(final CancelOrderDecoder cancel) {
        updateState(find(cancel.orderId()), OrderState.CANCELLED);
        triggerRun();
    }

    @Override
    public void onAckedOrder(final AckedOrderDecoder acked) {
        updateState(find(acked.orderId()), OrderState.ACKED);
        triggerRun();
    }

    @Override
    public void onCancelAckedOrder(final CancelAckedOrderDecoder cancelAcked) {
        updateState(find(cancelAcked.orderId()), OrderState.CANCELLED);
        triggerRun();
    }

    @Override
    public void onPendingOrder(final PendingOrderDecoder pending) {
        updateState(find(pending.orderId()), OrderState.PENDING);
        triggerRun();
    }

    public List<ChildOrder> children(){
        return this.children;
    }

    @Override
    public void onPartialFill(PartialFillOrderDecoder partialFill) {
        addChildFill(find(partialFill.orderId()), partialFill.quantity(), partialFill.price());
        triggerRun();
    }

    @Override
    public void onFill(FillOrderDecoder fill) {
        addChildFill(find(fill.orderId()), fill.quantity(), fill.price());
        triggerRun();
    }
}

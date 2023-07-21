package codingblackfemales.orderbook.order;

import codingblackfemales.collection.intrusive.IntrusiveLinkedListNode;

public class AbstractOrderFlyweight<TYPE extends IntrusiveLinkedListNode<TYPE>> extends IntrusiveLinkedListNode<TYPE> implements Order{

    @Override
    public long getPrice() {
        return 0;
    }

    @Override
    public long getQuantity() {
        return 0;
    }
}

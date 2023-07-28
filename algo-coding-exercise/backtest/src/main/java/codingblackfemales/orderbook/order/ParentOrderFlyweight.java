package codingblackfemales.orderbook.order;

import codingblackfemales.collection.intrusive.IntrusiveLinkedListNode;

public abstract class ParentOrderFlyweight<TYPE extends IntrusiveLinkedListNode<TYPE>> extends IntrusiveLinkedListNode<TYPE> implements Order{

    @Override
    public long getPrice() {
        return 0;
    }

    @Override
    public long getQuantity() {
        return 0;
    }

    public abstract void setQuantity(long quantity);


}

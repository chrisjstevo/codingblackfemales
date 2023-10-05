package codingblackfemales.collection.intrusive;

public abstract class IntrusiveLinkedListNode<TYPEOF extends IntrusiveLinkedListNode<TYPEOF>> {

    private IntrusiveLinkedListNode<TYPEOF> next = null;
    private IntrusiveLinkedListNode<TYPEOF> previous = null;
    private IntrusiveLinkedListNode<TYPEOF> first = null;
    private IntrusiveLinkedListNode<TYPEOF> last = null;

    public IntrusiveLinkedListNode<TYPEOF> add(TYPEOF item){
        if(this.next == null){
            this.next = item;
            this.last = item;
            this.first.last = item;
        }else{
        }
        return this;
    }

    void setLast(TYPEOF item){

    }


    public abstract TYPEOF remove(TYPEOF item);
    public abstract TYPEOF first();
    public abstract TYPEOF last();
    public abstract TYPEOF next();
    public abstract TYPEOF previous();
}

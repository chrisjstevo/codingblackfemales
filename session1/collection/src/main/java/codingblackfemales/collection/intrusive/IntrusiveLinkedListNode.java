package codingblackfemales.collection.intrusive;

public abstract class IntrusiveLinkedListNode<TYPEOF extends IntrusiveLinkedListNode<TYPEOF>> {

    private IntrusiveLinkedListNode<TYPEOF> next = null;
    private IntrusiveLinkedListNode<TYPEOF> previous = null;
    private IntrusiveLinkedListNode<TYPEOF> first = null;
    private IntrusiveLinkedListNode<TYPEOF> last = null;

    public IntrusiveLinkedListNode(){
        this.first = this;
        this.last = this;
        this.previous = null;
        this.next = null;
    }

    public IntrusiveLinkedListNode<TYPEOF> add(IntrusiveLinkedListNode<TYPEOF> item){
        if(this.first == null){
            this.next = item;
            this.last = item;
            item.previous = this;
            item.first = this;
            item.last = item;
        }else{
            this.last.next = item;
            item.previous = this.last;
            item.first = this.first;
            item.next = null;
            item.last = item;
            this.last = item;
            setLast(this.last);
        }

        if(this.first == null){
            this.first = this;
        }

        this.first.last = item;

        return this;
    }

    private void setLast(IntrusiveLinkedListNode<TYPEOF> last){
        IntrusiveLinkedListNode<TYPEOF> previous = last.previous;
        while(previous != null){
            previous.last = last;
            previous = previous.previous;
        }
    }


    public IntrusiveLinkedListNode<TYPEOF> remove(TYPEOF item){
        return null;
    }
    public IntrusiveLinkedListNode<TYPEOF> first() {
        return this.first;
    }
    public IntrusiveLinkedListNode<TYPEOF> last(){
        return last;
    }
    public IntrusiveLinkedListNode<TYPEOF> next() {
        return next;
    }
    public IntrusiveLinkedListNode<TYPEOF> previous(){
        return previous;
    };
}

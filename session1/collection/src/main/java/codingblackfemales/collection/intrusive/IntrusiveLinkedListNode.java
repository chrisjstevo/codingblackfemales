package codingblackfemales.collection.intrusive;

public abstract class IntrusiveLinkedListNode<TYPEOF extends IntrusiveLinkedListNode<TYPEOF>> {

    TYPEOF next = null;
    TYPEOF previous = null;
    TYPEOF first = null;
    TYPEOF last = null;

    private int size = 0;

    protected IntrusiveLinkedListNode(){
        this.first = (TYPEOF)this;
        this.last = (TYPEOF)this;
        this.previous = null;
        this.next = null;
        this.size = 1;
    }

    public IntrusiveLinkedListNode<TYPEOF> add(TYPEOF item){
        if(this.first == null){
            this.next = item;
            this.last = item;
            item.previous = (TYPEOF)this;
            item.first = (TYPEOF)this;
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
            this.first = (TYPEOF)this;
        }

        this.first.last = item;

        size += 1;
        setSize(this.last);

        return this;
    }

    private void setLast(TYPEOF last){
        if(last == null){
            return;
        }

        IntrusiveLinkedListNode<TYPEOF> thePrevious = last.previous;
        while(thePrevious != null){
            thePrevious.last = last;
            thePrevious = thePrevious.previous;
        }
    }
    private void setFirst(TYPEOF first){
        IntrusiveLinkedListNode<TYPEOF> thePrevious = first;
        while(thePrevious != null){
            thePrevious.first = first;
            thePrevious = thePrevious.next;
        }
    }

    private void setSize(TYPEOF last){
        if(last == null){
            return;
        }

        IntrusiveLinkedListNode<TYPEOF> thePrevious = last.previous;
        while(thePrevious != null){
            thePrevious.size = this.size;
            thePrevious = thePrevious.previous;
        }
    }

    public TYPEOF remove(){
        TYPEOF previous = this.previous;
        TYPEOF next = this.next;
        if(previous != null){
            previous.next = next;
        }

        if(this.last.equals(this)){
            this.last = previous;
            if(this.previous != null){
                this.previous.last = previous;
            }
        }

        if(this.first == null){
            return null;
        }

        if(this.first.equals(this)){
            this.first = next;
        }

        size -= 1;



        setSize(this.last);
        setFirst(this.first);
        setLast(this.last);
        return this.first;
    }
    public TYPEOF first() {
        return this.first;
    }
    public TYPEOF last(){
        return last;
    }
    public TYPEOF next() {
        return next;
    }
    public TYPEOF previous(){
        return previous;
    };

    public void next(TYPEOF node){

        this.next = node;
    }

    public int size(){
        TYPEOF working = this.first;
        int count = 0;
        while(working!=null){
            count += 1;
            working = working.next;
        }

        return count;
    }
}

package codingblackfemales.collection.intrusive;

import java.util.ArrayList;

public abstract class IntrusiveLinkedListNode<TYPEOF extends IntrusiveLinkedListNode<TYPEOF>> {

    TYPEOF next = null;
    TYPEOF previous = null;
    TYPEOF first = null;
    TYPEOF last = null;

    public IntrusiveLinkedListNode(){
        this.first = (TYPEOF)this;
        this.last = (TYPEOF)this;
        this.previous = null;
        this.next = null;
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

        return this;
    }

    private void setLast(TYPEOF last){
        IntrusiveLinkedListNode<TYPEOF> previous = last.previous;
        while(previous != null){
            previous.last = last;
            previous = previous.previous;
        }
    }


    public TYPEOF remove(){
        TYPEOF previous = this.previous;
        TYPEOF next = this.next;
        if(previous != null){
            previous.next = next;
        }

        if(this.first.equals(this)){
            this.first = next;
        }
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
}

package codingblackfemales.collection.intrusive;

/**
 * A basic implementation of an Intrusive Linked List. Some features of this:
 *
 * 1) The remove call always returns the new first node
 * 2)
 *
 *
 * @param <TYPEOF>
 */
public abstract class IntrusiveLinkedListNode<TYPEOF extends IntrusiveLinkedListNode<TYPEOF>> {

    protected TYPEOF next = null;
    protected TYPEOF previous = null;
    protected TYPEOF first = null;
    protected TYPEOF last = null;

    protected int size = 0;

    protected IntrusiveLinkedListNode() {
        this.first = (TYPEOF) this;
        this.last = (TYPEOF) this;
        this.previous = null;
        this.next = null;
        this.size = 1;
    }

    public IntrusiveLinkedListNode<TYPEOF> add(final TYPEOF item) {
        addToTail(item);
        setPrevious(item, this.last);
        setLast(item);
        item.first = this.first;
        setSize(this.size() + 1);
        return first();
    }

    private void setPrevious(final TYPEOF item, final TYPEOF previous){
        item.previous = previous;
    }

    private void addToTail(final TYPEOF item){
        this.last.next = item;
    }

    private void setLast(final TYPEOF item){
        this.first.last = item;
    }

    private void setSize(final int size){
        this.first.size = size;
    }

    private void setSize(final TYPEOF newFirst, final int size){
        newFirst.size = size;
    }

    private void linkPreviousAndNext(){
        TYPEOF previous = this.previous;
        TYPEOF next = this.next;

        if (previous != null) {
            previous.next = next;
        }
    }

    private void setFirst(TYPEOF first) {
        this.first.first = first;
//        IntrusiveLinkedListNode<TYPEOF> thePrevious = first;
//        while (thePrevious != null) {
//            thePrevious.first = first;
//            thePrevious = thePrevious.next;
//        }
    }

    private void resetFirst(TYPEOF first) {
        //this.first.first = first;
        IntrusiveLinkedListNode<TYPEOF> thePrevious = first;
        while (thePrevious != null) {
            thePrevious.first = first;
            thePrevious = thePrevious.next;
        }
    }

//    private void setSize(TYPEOF last) {
//        if (last == null) {
//            return;
//        }
//
//        IntrusiveLinkedListNode<TYPEOF> thePrevious = last.previous;
//        while (thePrevious != null) {
//            thePrevious.size = this.size;
//            thePrevious = thePrevious.previous;
//        }
//    }

    public TYPEOF remove() {

        TYPEOF previousLast = this.last();

        final int newSize = this.size() - 1;

        if(previousLast.equals(this)){
            setLast(this.previous);
        }

        linkPreviousAndNext();

        TYPEOF newFirst = null;

        if (this.first.equals(this)) {
            newFirst = next;
            resetFirst(newFirst);
        }else{
            newFirst = this.first();
        }

        if(newFirst != null){
            setSize(newFirst, newSize);
            return newFirst;
        }else{
            return null;
        }
    }

    public TYPEOF first() {
        return this.first.first;
    }

    public TYPEOF last() {
        return this.first.last;
    }

    public TYPEOF next() {
        return next;
    }

    public TYPEOF previous() {
        return previous;
    }

    public void next(TYPEOF node) {
        this.next = node;
    }

    public int size() {
        return this.first.size;
    }
}

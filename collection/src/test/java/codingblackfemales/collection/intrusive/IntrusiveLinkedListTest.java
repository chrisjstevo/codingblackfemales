package codingblackfemales.collection.intrusive;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class IntrusiveLinkedListTest {

    class ExampleNode extends IntrusiveLinkedListNode<ExampleNode> {
        private int i = 0;
        ExampleNode(int i){
            super();
            this.i = i;
        }
        int getI() {
            return i;
        }

        @Override
        public String toString() {
            return "ExampleNode(" + i + ")";
        }
    }

    @Test
    public void testSimpleOps(){

        final ExampleNode node = (ExampleNode) new ExampleNode(1)
                .add(new ExampleNode(2))
                .add(new ExampleNode(3));

        assertEquals(3, node.size());

        ExampleNode newNode = node.next.remove();

        assertEquals(2, newNode.size());

        assertEquals(1, newNode.getI());
        assertEquals(3, newNode.next.getI());
    }

    @Test
    public void testFirstNodeIsAlwaysCorrect(){

        final ExampleNode node = new ExampleNode(0);

        assertEquals(1, node.size());
        assertEquals(0, node.first.getI());
        assertEquals(node, node.first);
        assertEquals(node, node.last);

        IntStream.range(1, 5).forEach( i -> {
            node.add(new ExampleNode(i));
        });

        assertEquals(4, node.last.getI());
        assertEquals(0, node.first.getI());
        assertEquals(5, node.size());

        //when we remove a node we always return latest head.
        ExampleNode latestHead = node.first.next.remove();

        assertEquals(0, latestHead.first.getI());
        assertEquals(4, latestHead.last.getI());
        assertEquals(4, latestHead.size());

        assertEquals(2, latestHead.next.getI());
        assertEquals(3, latestHead.next.next.getI());
    }

    @Test
    public void testChangeLast(){

        final ExampleNode node = new ExampleNode(0);

        IntStream.range(1, 5).forEach( i -> {
            node.add(new ExampleNode(i));
        });

        final ExampleNode newNodeMinus1Last = node.first.last.remove();
        final ExampleNode expectedLast1 = node.next.next.next;

        assertEquals(3, expectedLast1.getI());
        assertEquals(4, newNodeMinus1Last.size());

        assertAllLastEquals(newNodeMinus1Last, expectedLast1, 3);

        final ExampleNode newFirst2 = node.first.last.remove();
        final ExampleNode newLast2 = newFirst2.next.next;

        assertEquals(3, newFirst2.size());
        assertEquals(2, newLast2.getI());

        assertAllLastEquals(newFirst2, newLast2, 2);

        final ExampleNode newFirst3 = node.first.last.remove();
        final ExampleNode newLast3 = newFirst3.next;

        assertEquals(2, newFirst3.size());
        assertEquals(1, newLast3.getI());

        assertAllLastEquals(newFirst3, newLast3, 1);

        final ExampleNode newFirst4 = node.first.last.remove();
        final ExampleNode newLast4 = newFirst4;

        assertEquals(1, newFirst4.size());
        assertEquals(0, newLast4.getI());

        assertAllLastEquals(newFirst4, newLast4, 0);

        assertEquals(newFirst4, newLast4);
    }

    @Test
    public void testChangeFirst(){
        final ExampleNode node = new ExampleNode(0);

        IntStream.range(1, 5).forEach( i -> {
            node.add(new ExampleNode(i));
        });

        final ExampleNode node1First = node.first.remove();
        assertEquals(1, node1First.first.getI());
        assertEquals(4, node1First.size());

        final ExampleNode node2First = node1First.first.remove();
        assertEquals(2, node2First.first.getI());
        assertEquals(3, node2First.size());

        final ExampleNode node3First = node2First.first.remove();
        assertEquals(3, node3First.first.getI());
        assertEquals(2, node3First.size());

        final ExampleNode node4First = node3First.first.remove();
        assertEquals(4, node4First.first.getI());
        assertEquals(1, node4First.size());
    }

    private static void assertAllLastEquals(ExampleNode starting, ExampleNode expectedLast, int expectedLastId){
        ExampleNode next = starting;

        while (next != null) {
            assertEquals(next.last(), expectedLast);
            assertEquals(expectedLastId, expectedLast.getI());
            next = next.next();
        }
    }

}

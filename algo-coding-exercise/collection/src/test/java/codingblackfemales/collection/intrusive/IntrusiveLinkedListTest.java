package codingblackfemales.collection.intrusive;

import org.junit.Test;

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
    }

    @Test
    public void testNormalOperations(){

        ExampleNode node = (ExampleNode) new ExampleNode(1)
                .add(new ExampleNode(2))
                .add(new ExampleNode(3));

        assertEquals(3, node.size());

        node.next.remove();

        assertEquals(2, node.size());

        assertEquals(1, node.getI());
        assertEquals(3, node.next.getI());
    }

}

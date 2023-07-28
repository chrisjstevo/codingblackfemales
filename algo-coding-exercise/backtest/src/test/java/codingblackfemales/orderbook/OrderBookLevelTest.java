package codingblackfemales.orderbook;

import org.junit.Assert;
import org.junit.Test;

public class OrderBookLevelTest {

    @Test
    public void testIntrusiveBookLevel(){

        final OrderBookLevel level1 = new OrderBookLevel();
        level1.setQuantity(100_000);
        level1.setPrice(100);
        final OrderBookLevel level2 = new OrderBookLevel();
        level2.setQuantity(150_000);
        level2.setPrice(98);
        final OrderBookLevel level3 = new OrderBookLevel();
        level3.setQuantity(180_000);
        level3.setPrice(97);
        final OrderBookLevel level4 = new OrderBookLevel();
        level4.setQuantity(200_000);
        level4.setPrice(96);

        level1.add(level2)
              .add(level3)
              .add(level4);

        Assert.assertEquals(level1.last(), level4);
        Assert.assertNull(level1.previous());
        Assert.assertEquals(level1.next(), level2);

        Assert.assertEquals(level2.first(), level1);
        Assert.assertEquals(level2.next(), level3);
        Assert.assertEquals(level2.previous(), level1);
        Assert.assertEquals(level2.last(), level4);

        Assert.assertEquals(level3.next(), level4);
        Assert.assertEquals(level3.previous(), level2);
        Assert.assertEquals(level3.last(), level4);

        Assert.assertNull(level4.next());
        Assert.assertEquals(level4.previous(), level3);
        Assert.assertEquals(level4.last(), level4);

        System.out.println(level1.toString());

        var existingLevel1 = level3.remove();

        Assert.assertEquals(existingLevel1, level1);

        System.out.println(level1.toString());

        var newLevel1 = level1.remove();

        System.out.println(newLevel1.toString());

        Assert.assertEquals(newLevel1, level2);
    }

}

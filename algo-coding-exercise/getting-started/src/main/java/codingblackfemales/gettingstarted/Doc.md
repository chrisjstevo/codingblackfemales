## The Logic
* Do not place more than 5 orders 
* Buy a given quantity (e.g. 2000 units, defined in MyAlgoLogic)
* Buy at the given price, but not more than myMaxBid (e.g. price+10)
* If filledQuantity != quantity when you reach maxPrice, place the rest on the passive side of the book

## MyAlgoLogic.java
- The first if statement ensures that orders are not more than 5
- The second if statement: When the program starts, this is the first block it should execute, thus making it possible to be able to get filledQuantity in the subsequent block. 
If the price matches, it continues, else it places on the passive side and ends.
    - The next block (else-if) executes if there's an active order and my price matches. It then gets the filledQuantity, and adjusts the quantityToBuy based on that. If there's still units to be bought (quantityToBuy != 0), then it creates an order, else does nothing.

### What if price doesn't match?
The last else block should execute when the price doesn't match. It should adjust the filled quantity, and place order.

## The Tests
MyAlgoTest works in isolation of the market data, so filledQuantity is always 0, and it continues to place orders until there are five orders.

MyAlgoBackTest responds to market data, so quantity is adjusted based on filledQuantity.
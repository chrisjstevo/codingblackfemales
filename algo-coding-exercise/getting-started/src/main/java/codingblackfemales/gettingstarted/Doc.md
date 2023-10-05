## The Logic
* Do not place more than 5 orders 
* Buy a given quantity (e.g. 2000 units, defined in MyAlgoLogic)
* Buy at the given price

## MyAlgoLogic.java
- The first if statement ensures that orders are not more than 5
- The second if statement updates the filledQuantity (which can only be accessed after an order has been placed)
- The last if-else block computes the logic:
    - if price matches, and quantity is not 0 or a negative number,<span style="color:green"> place order </span>
    - if price matches, and quantity is 0 or a negative number,  <span style="color:red"> do not place order </span>
    - if price doesn't match, and quantity is not 0 or a negative number, <span style="color:green">place order </span>
    - if price doesn't match, and quantity is 0, <span style="color:red"> do not place order </span>

## The Tests
- MyAlgoTest works in isolation of the market data, so filledQuantity is always 0, and it continues to place orders until there are five orders (or one order in a case where price is less than farTouch.price).

- MyAlgoBackTest responds to market data, so quantity is adjusted based on filledQuantity.
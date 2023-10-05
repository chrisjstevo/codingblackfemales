## The Logic
* Do not place more than 5 orders.
* Buy a given quantity (e.g. 2000 units, defined in MyAlgoLogic. This represents the quantity to buy on each iteration, it is defined above as 5, so totalQuantityToBuy = 10,000)
* Buy at the given price

## MyAlgoLogic.java
- The first if statement ensures that orders are not more than 5
- The second if-else block computes the logic:
    - if price matches, and quantity is not 0 or a negative number,<span style="color:green"> place order </span>
    - if price matches, and quantity is 0 or a negative number,  <span style="color:red"> do not place order </span>
    - if price doesn't match, and quantity is not 0 or a negative number, <span style="color:green">place order </span>
    - if price doesn't match, and quantity is 0, <span style="color:red"> do not place order </span>

## The Tests
- MyAlgoTest works in isolation of the market data, so filledQuantity is always 0, and it continues to place orders until there are five orders.

- MyAlgoBackTest responds to market data, and filledQuantity is adjusted based on this.
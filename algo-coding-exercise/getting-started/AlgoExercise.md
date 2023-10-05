# My Algo Exercise Documentation 

This trading algorithm creates child orders at the average price of the market prices available. 

To make it more reflective of a real life market, if the quantity desired exceeds a certain percentage of the overall market volume, no action occurs and no child order is created. 

##### *NOTE*: When trading, you want to be as descrete as possible, causing [minimal](https://www.investopedia.com/terms/v/volumeoftrade.asp) impact to the market. 

### [MyAlgoLogic](https://github.com/Teyiowuawi/codingblackfemales/blob/main/algo-coding-exercise/getting-started/src/main/java/codingblackfemales/gettingstarted/MyAlgoLogic.java)

A for loop is used to iterate through the tick()'s using the `getBidLevels()` method on the `state` object to get the number of bid levels available:

```java
long totalTickPrice 
```

Holds the total price of the tick() after iteration 

```java
long totalTickQuantity 
```
Holds the total volume quantity of the tick() after iteration


Following this, `averageTickPrice` is calculated using the `totalTickQuantity` and `state.getBidLevels()`

```java
long quantity 
```
Is the quantity desired

```java 
long percentageOfMarketolumeNotToExceed
```

Is the percentage threshold of market volume to stay below. In this algo it is 20% however, figures in real life may be significantly lower.  

If `quantity` doesn't exceed the `percentageOfMarketVolumeNotToExceed`, three child orders are created. 

### [MyAlgoTest](https://github.com/Teyiowuawi/codingblackfemales/blob/main/algo-coding-exercise/getting-started/src/test/java/codingblackfemales/gettingstarted/MyAlgoTest.java)

In this file, there are various tick()'s created to test my algorithms against. Depending on the quantity and the percentage threshold of market volume to trade under, either 3 child orders at the average tick price are created or no action occurs. 

**Example** 

```java 
.next().price(97L).size(5000L)
.next().price(99L).size(10000L)
.next().price(101L).size(15000L)
.next().price(103L).size(20000L);
```
2000 is below 20% of the market volume of this tick()(10000) therefore, 3 child orders (quantity 2000 @ price 100) are created and added to the Bid side of the book. 

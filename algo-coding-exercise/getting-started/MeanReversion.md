## Mean Reversion Algorithm

#### Introduction
The algorithm creates and cancels child orders using the mean reversion trading strategy. <br>

The Mean Reversion strategy suggests that an asset price will revert to the average price over time. <br>
This algorithm aims to make a profit, i.e., buying low and selling high by monitoring how far the current price deviates from the mean price. <br>

The algorithm uses the getMean method, to calculate the average price from the closing prices (if Available) or SELL/ASK side using the last N orders. It then sets a threshold based on the average price. This threshold represents an acceptable price deviation from the mean. <br>

The algorithm creates a new BID/BUY child order if the best SELL/ASK price is less than the average price minus the threshold. This ensures we buy at a price lower than the average price. <br>

The algorithm cancels a BID /BUY child order (if present and not filled) if the order price is greater than the average price plus the threshold. <br>
It then creates a SELL/ASK child order if any of the filled child order prices is less than the average price. This ensures that we sell at a price higher than we bought the stock. <br>

#### Input and Output
The algorithm takes as input the state of the order book.<br>

The algorithm can produce the following actions:<br>
-	CreateChildOrder: To create a new child order with a specified side, quantity and price.
-	CancelChildOrder: To cancel a specific child order.
- NoAction: To take no action if none of the conditions are met. <br>


The algorithm has exit conditions to avoid creating unnecessary orders or canceling orders when not required.


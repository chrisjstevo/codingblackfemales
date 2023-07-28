# Coding Black Females - Create your own Trading Algo

Welcome to your first Electronic Trading Coding Challenge!

### The Objective

The objective of this challenge is to write a simple trading algo that creates and cancels child orders. 

### How to Get Started

Pre-requisites: 

1. The project requires java version 16 or higher
2. You should have latest Apache Maven installed or an IDE which has embedded in it (like IntelliJ)

Opening the project: 

1. Git clone this project
2. Open the project as a maven project in your IDE
3. Open the "getting-started" model.
4. Navigate to the MyAlgoTest
5. You're ready to go!

### Writing Your Algo

At this point its worth *taking a deep breath*. There is a lot of code in this repository, but 99% of it is framework to help you. 

To get started, look at the the exampes below: 

* Algo Logic [AddCancelAlgoLogic.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/algo/src/main/java/codingblackfemales/algo/AddCancelAlgoLogic.java) Back Test: [AddCancelAlgoBackTest.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/backtest/src/test/java/codingblackfemales/backtest/AddCancelAlgoBackTest.java)
* Algo Logic: [PassiveAlgoLogic.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/algo/src/main/java/codingblackfemales/algo/PassiveAlgoLogic.java) Unit Test: [PassiveAlgoTest.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/algo/src/test/java/codingblackfemales/algo/PassiveAlgoTest.java) Back Test: [PassiveAlgoBackTest.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/backtest/src/test/java/codingblackfemales/backtest/PassiveAlgoBackTest.java)
* Algo Logic: [SniperAlgoLogic.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/algo/src/main/java/codingblackfemales/algo/SniperAlgoLogic.java) Back Test: [SniperAlgoBackTest.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/backtest/src/test/java/codingblackfemales/backtest/SniperAlgoBackTest.java)

You can see from these examples the algo has access to a state object (SimpleAlgoState state), that is passed in from the container. The state object gives you access to two sets of information: 

1. A current view on the market data
2. The current view of the child orders you've created, and whether they are filled or cancelled etc..

https://github.com/chrisjstevo/codingblackfemales/blob/263cecf4da4a3afa4b94a021f82265f3fcafac08/algo-coding-exercise/algo/src/main/java/codingblackfemales/algo/PassiveAlgoLogic.java#L27-L30

You can see in the above code snippet the PassiveAlgoLogic getting access to the market data on the bid side of the book. It then uses that price to place a passive order into the bid side of the order book. 

![cbf-graphics-overview](https://github.com/chrisjstevo/codingblackfemales/assets/17289809/f9a27f2a-5c9b-4b9e-bbea-762a6a144868)

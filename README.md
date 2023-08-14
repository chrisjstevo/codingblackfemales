# Coding Black Females - Create your own Trading Algo

Welcome to your first Electronic Trading Coding Challenge!

### The Objective

The objective of this challenge is to write a simple trading algo that creates and cancels child orders. 

**Stretch objective:** write an algo that can make money buy buying shares when the order book is cheaper, and selling them when the order book is more expensive. 

Note: make sure you think about how the market data could change over time, add scenarios into your test to show how you've tested those scenarios. 

### How to Get Started

Pre-requisites: 

1. The project requires java version 16 or higher
2. You should have latest Apache Maven installed or an IDE which has it embedded within it (like IntelliJ)

Opening the project: 

1. Git fork this project
2. Open the project as a maven project in your IDE (normally by opening the top level pom.xml file)
3. Click to expand the "getting-started" module
4. Navigate to the [MyAlgoTest.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/getting-started/src/main/java/codingblackfemales/gettingstarted/MyAlgoLogic.java) and [MyAlgoLogic.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/getting-started/src/main/java/codingblackfemales/gettingstarted/MyAlgoLogic.java)
5. You're ready to go!

** Please note, you will need to run the "mvn compile" task, either at command line or from the IDE integration to make sure the binary encoders and decoders are created. 

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

https://github.com/chrisjstevo/codingblackfemales/blob/263cecf4da4a3afa4b94a021f82265f3fcafac08/algo-coding-exercise/algo/src/main/java/codingblackfemales/algo/PassiveAlgoLogic.java#L33-L36

### An Overview of the Backtesting Infra

The back testing infrastructure allows you, from within a unit test, to write an algo that adds or removes orders into an order book. When your orders go onto the orer book, if they can't match immediately (i.e. the price is too passive) the order book will send a market data update showing the new order book with your quantity in it. Your algo can then see that market data update and respond to it. 

When you are writing scenarios to see how the algo would behave you can inject new market data by creating copies of the tick() method and changing the price or quantity values it submits. 

There is one example in the test provided already: 

https://github.com/chrisjstevo/codingblackfemales/blob/ec5bbff1a3d4ed07eddaae5a8fcca928ad5c56f4/algo-coding-exercise/getting-started/src/test/java/codingblackfemales/gettingstarted/MyAlgoTest.java#L27-L33

The below diagram shows the message flows across the infrastructure. If you look at the [AbstractAlgoBackTest.java](https://github.com/chrisjstevo/codingblackfemales/blob/main/algo-coding-exercise/getting-started/src/test/java/codingblackfemales/gettingstarted/AbstractAlgoBackTest.java) you can see how this is wired together for you behind the scenes. 

![cbf-graphics-overview](https://github.com/chrisjstevo/codingblackfemales/assets/17289809/f9a27f2a-5c9b-4b9e-bbea-762a6a144868)

In the diagram you can see your algo (MyAlgoLogic) in the darker blue box. That is where you add your logic to create or cancel orders. 

When you're orders are created they travel through a Sequencer component which duplicates the message out to each consumer. The sequencer distributes all messages  (including your createTick() message) to all consumers.

The orders then hit the order book component, the order book checks if this order can match with any other in the book (including fake orders that come from our market data tick). If it cannot immediately match, it adds the order to the order book and sends out an updated market data message showing the new quantities in the order book. The algo container will then get this updated message and see the new view of the orderbook in its state. 

If the order book can match the order immediately, it will send out a fill message and then publish a new market data message of the order book with the matched quantity removed. 

### What are the most important parts of this?

Writing tests that can assert how your algo behaves, sometimes you will get stuff or have bugs that means your code doesn't work properly, but having tests showing what you were trying to do is (almost) as good as having the whole thing work. 

### Good Luck!

Remember your mentors are here to help

### Frequently Asked Questions

1. I am getting compile errors for the encoders in the project, how do I resolve this?

The encoders and decoders are generated from the .xsd documents in the project. To get them generated, you must run a "mvn compile" step either from inside the IDE or from the command line. 








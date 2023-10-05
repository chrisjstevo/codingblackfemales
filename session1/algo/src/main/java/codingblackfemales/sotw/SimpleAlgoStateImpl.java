package codingblackfemales.sotw;

import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;

import java.util.List;

public class SimpleAlgoStateImpl implements SimpleAlgoState {

    public final MarketDataService marketDataService;
    public final OrderService orderService;

    public SimpleAlgoStateImpl(final MarketDataService marketDataService, final OrderService orderService) {
        this.marketDataService = marketDataService;
        this.orderService = orderService;
    }

    @Override
    public int getBidLevels() {
        return marketDataService.getBidLength();
    }

    @Override
    public int getAskLevels() {
        return marketDataService.getAskLength();
    }

    @Override
    public BidLevel getBidAt(int index) {
        return marketDataService.getBidLevel(index);
    }

    @Override
    public AskLevel getAskAt(int index) {
        return marketDataService.getAskLevel(index);
    }

    @Override
    public List<Order> getChildOrders() {
        return null;
    }
}

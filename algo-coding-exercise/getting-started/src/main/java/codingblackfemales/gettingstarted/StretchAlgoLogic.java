package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import codingblackfemales.sotw.SimpleAlgoState;
import messages.order.Side;

import java.util.Objects;
import java.util.function.LongSupplier;

public class StretchAlgoLogic implements AlgoLogic {

    public enum EntryMode {
        PASSIVE_BID,
        TAKE_ASK
    }

    static final long QUANTITY = 10L;
    static final long MAX_BUY_PRICE = 98L;
    static final long MIN_SELL_PRICE = 102L;
    static final long ORDER_TIMEOUT_MS = 30_000L;

    private final LongSupplier clock;
    private final EntryMode entryMode;

    private boolean buySubmitted;
    private boolean buyCancelSubmitted;
    private boolean sellSubmitted;
    private boolean sellCancelSubmitted;

    private long buySubmittedAt;
    private long sellSubmittedAt;

    public StretchAlgoLogic() {
        this(System::currentTimeMillis, EntryMode.TAKE_ASK);
    }

    public StretchAlgoLogic(LongSupplier clock, EntryMode entryMode) {
        this.clock = Objects.requireNonNull(clock);
        this.entryMode = Objects.requireNonNull(entryMode);
    }

    @Override
    public Action evaluate(SimpleAlgoState state) {
        ChildOrder buy = findOrder(state, Side.BUY);
        ChildOrder sell = findOrder(state, Side.SELL);

        // Manage an existing buy before considering another action.
        if (buySubmitted) {
            if (buy == null) {
                return NoAction.NoAction;
            }

            if (isWorking(buy)) {
                if (!buyCancelSubmitted
                        && clock.getAsLong() - buySubmittedAt
                        >= ORDER_TIMEOUT_MS) {

                    buyCancelSubmitted = true;
                    return new CancelChildOrder(buy);
                }

                return NoAction.NoAction;
            }
        }

        // One sell attempt. Cancel its remainder if it times out.
        if (sellSubmitted) {
            if (sell != null
                    && isWorking(sell)
                    && !sellCancelSubmitted
                    && clock.getAsLong() - sellSubmittedAt
                    >= ORDER_TIMEOUT_MS) {

                sellCancelSubmitted = true;
                return new CancelChildOrder(sell);
            }

            return NoAction.NoAction;
        }

        // Missing prices should not prevent the timeout checks above.
        if (!hasValidBook(state)) {
            return NoAction.NoAction;
        }

        var bid = state.getBidAt(0);
        var ask = state.getAskAt(0);

        if (!buySubmitted) {
            // This demonstration expects its own initially empty state.
            if (!state.getChildOrders().isEmpty()) {
                return NoAction.NoAction;
            }

            long buyPrice = entryMode == EntryMode.PASSIVE_BID
                    ? bid.getPrice()
                    : ask.getPrice();

            if (buyPrice > MAX_BUY_PRICE) {
                return NoAction.NoAction;
            }

            // For an immediate entry, require enough displayed quantity.
            // This check does not guarantee an execution.
            if (entryMode == EntryMode.TAKE_ASK
                    && ask.getQuantity() < QUANTITY) {
                return NoAction.NoAction;
            }

            buySubmitted = true;
            buySubmittedAt = clock.getAsLong();

            return new CreateChildOrder(
                    Side.BUY, QUANTITY, buyPrice
            );
        }

        // The buy is now filled or cancelled.
        // A partly filled, cancelled buy still leaves a position.
        long position = buy.getFilledQuantity();

        if (position > 0
                && bid.getPrice() >= MIN_SELL_PRICE
                && bid.getQuantity() >= position) {

            sellSubmitted = true;
            sellSubmittedAt = clock.getAsLong();

            return new CreateChildOrder(
                    Side.SELL, position, bid.getPrice()
            );
        }

        return NoAction.NoAction;
    }

    public Action onTimer(SimpleAlgoState state) {
        return evaluate(state);
    }

    private boolean isWorking(ChildOrder order) {
        return order.getState() != OrderState.CANCELLED
                && order.getFilledQuantity() < order.getQuantity();
    }

    private ChildOrder findOrder(SimpleAlgoState state, Side side) {
        for (ChildOrder order : state.getChildOrders()) {
            if (order.getSide() == side) {
                return order;
            }
        }
        return null;
    }

    private boolean hasValidBook(SimpleAlgoState state) {
        if (state.getBidLevels() == 0 || state.getAskLevels() == 0) {
            return false;
        }

        var bid = state.getBidAt(0);
        var ask = state.getAskAt(0);

        return bid != null
                && ask != null
                && bid.getPrice() > 0
                && ask.getPrice() > 0
                && bid.getQuantity() > 0
                && ask.getQuantity() > 0
                && bid.getPrice() < ask.getPrice();
    }
}
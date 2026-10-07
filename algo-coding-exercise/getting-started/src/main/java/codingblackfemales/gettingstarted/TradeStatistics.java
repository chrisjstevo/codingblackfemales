package codingblackfemales.gettingstarted;

import codingblackfemales.sotw.OrderState;
import codingblackfemales.sotw.SimpleAlgoState;
import messages.order.Side;

public class TradeStatistics {

    public record Snapshot(
            int orders,
            int cancelledOrders,
            long bought,
            long sold,
            long position,
            double quantityFillRate,
            double averageBuyPrice,
            double realizedGross,
            double unrealizedGross,
            double fees
    ) {
        public double totalNet() {
            return realizedGross + unrealizedGross - fees;
        }
    }

    public static Snapshot calculate(
            SimpleAlgoState state,
            long currentBid,
            double feePerFilledUnit
    ) {
        if (!Double.isFinite(feePerFilledUnit)
                || feePerFilledUnit < 0) {
            throw new IllegalArgumentException("Invalid fee");
        }

        long bought = 0;
        long sold = 0;
        long buyValue = 0;
        long sellValue = 0;
        long submittedQuantity = 0;
        long filledQuantity = 0;
        int cancelled = 0;

        for (var order : state.getChildOrders()) {
            submittedQuantity += order.getQuantity();
            filledQuantity += order.getFilledQuantity();

            if (order.getState() == OrderState.CANCELLED) {
                cancelled++;
            }

            if (order.getSide() == Side.BUY) {
                bought += order.getFilledQuantity();
                buyValue += order.getFilledValue();
            } else if (order.getSide() == Side.SELL) {
                sold += order.getFilledQuantity();
                sellValue += order.getFilledValue();
            }
        }

        if (sold > bought) {
            throw new IllegalStateException(
                    "This strategy must not sell more than it bought"
            );
        }

        long position = bought - sold;

        if (position > 0 && currentBid <= 0) {
            throw new IllegalArgumentException(
                    "A positive bid is needed to value an open position"
            );
        }

        double averageBuy = bought == 0
                ? 0.0
                : (double) buyValue / bought;

        double realized = sellValue - sold * averageBuy;
        double unrealized = position * (currentBid - averageBuy);
        double fees = (bought + sold) * feePerFilledUnit;

        double fillRate = submittedQuantity == 0
                ? 0.0
                : (double) filledQuantity / submittedQuantity;

        return new Snapshot(
                state.getChildOrders().size(),
                cancelled,
                bought,
                sold,
                position,
                fillRate,
                averageBuy,
                realized,
                unrealized,
                fees
        );
    }
}
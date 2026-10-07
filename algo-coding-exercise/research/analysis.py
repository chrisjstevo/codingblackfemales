import argparse
from pathlib import Path

import numpy as np
import pandas as pd
from sklearn.linear_model import LinearRegression, LogisticRegression
from sklearn.metrics import mean_absolute_error, brier_score_loss
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler


FEATURES = ["spread", "momentum", "imbalance"]
HORIZON = 30
ROUND_TRIP_COST = 0.40  # Example cost per unit: 0.20 buying + 0.20 selling.


def prepare_data(quotes):
    """Validate quotes and create model inputs and outcomes."""
    required = [
        "timestamp_ms",
        "bid",
        "ask",
        "bid_quantity",
        "ask_quantity",
    ]

    missing = set(required) - set(quotes.columns)
    if missing:
        raise ValueError(f"Missing CSV columns: {sorted(missing)}")

    data = quotes[required].copy()

    for column in required:
        data[column] = pd.to_numeric(data[column], errors="raise")

    if not np.isfinite(data.to_numpy(dtype=float)).all():
        raise ValueError("Quotes contain missing or infinite values.")

    # Our Java exporter produces exactly one observation per second.
    intervals = data["timestamp_ms"].diff().dropna()
    if not intervals.eq(1_000).all():
        raise ValueError("Expected timestamps exactly 1,000 ms apart.")

    valid = (
            (data["bid"] > 0)
            & (data["ask"] > data["bid"])
            & (data["bid_quantity"] > 0)
            & (data["ask_quantity"] > 0)
    )
    if not valid.all():
        raise ValueError("Quotes must have positive values and bid < ask.")

    midpoint = (data["bid"] + data["ask"]) / 2

    # Inputs: information available at the current observation.
    data["spread"] = data["ask"] - data["bid"]
    data["momentum"] = midpoint - midpoint.shift(10)
    data["imbalance"] = (
            (data["bid_quantity"] - data["ask_quantity"])
            / (data["bid_quantity"] + data["ask_quantity"])
    )

    # Outcome: a hypothetical sale 30 observations/seconds later.
    data["target_timestamp_ms"] = data["timestamp_ms"].shift(-HORIZON)
    data["margin"] = (
            data["bid"].shift(-HORIZON)
            - data["ask"]
            - ROUND_TRIP_COST
    )

    # Remove rows without enough past or future observations.
    data = data.dropna().copy()
    data["profitable"] = (data["margin"] > 0).astype(int)

    return data


def split_data(data):
    """Train on earlier observations and test on later observations."""
    if len(data) < 100:
        raise ValueError("Need at least 100 usable observations.")

    cutoff = data["timestamp_ms"].iloc[int(len(data) * 0.70)]

    # Exclude training rows whose future outcomes reach the test period.
    train = data.loc[data["target_timestamp_ms"] < cutoff].copy()
    test = data.loc[data["timestamp_ms"] >= cutoff].copy()

    if train.empty or test.empty:
        raise ValueError("Not enough observations for the time split.")

    return train, test


def describe_selection(name, data, selected):
    """Describe quote opportunities, not executed trading profits."""
    margins = data.loc[selected, "margin"]

    if margins.empty:
        print(f"{name}: 0 observations selected")
        return

    print(
        f"{name}: {len(margins)} observations, "
        f"positive margin share={(margins > 0).mean():.1%}, "
        f"mean margin per unit={margins.mean():.3f}"
    )


def run(csv_path):
    quotes = pd.read_csv(csv_path)
    data = prepare_data(quotes)
    train, test = split_data(data)

    if train["profitable"].nunique() < 2:
        raise ValueError(
            "Training data needs both positive and non-positive margins."
        )

    x_train = train[FEATURES]
    x_test = test[FEATURES]

    # Scaling is learned from training data only.
    regression = make_pipeline(
        StandardScaler(),
        LinearRegression(),
    )
    classifier = make_pipeline(
        StandardScaler(),
        LogisticRegression(max_iter=1_000),
    )

    regression.fit(x_train, train["margin"])
    classifier.fit(x_train, train["profitable"])

    predicted_margin = regression.predict(x_test)
    predicted_probability = classifier.predict_proba(x_test)[:, 1]

    # Simple comparisons: does learning improve on these baselines?
    unchanged_price_margin = (
            test["bid"] - test["ask"] - ROUND_TRIP_COST
    )
    constant_probability = np.full(
        len(test),
        train["profitable"].mean(),
    )

    print("Data source: synthetic quotes from ResearchDataExport")
    print(f"CSV rows: {len(quotes)}")
    print(f"Training rows: {len(train)}")
    print(f"Test rows: {len(test)}")

    print("\nTraining-data summary:")
    print(
        train[FEATURES + ["margin"]]
        .describe()
        .round(3)
        .to_string()
    )

    print("\nRegression: mean absolute error — lower is better")
    print(
        "Unchanged-price baseline: "
        f"{mean_absolute_error(test['margin'], unchanged_price_margin):.3f}"
    )
    print(
        "Linear regression: "
        f"{mean_absolute_error(test['margin'], predicted_margin):.3f}"
    )

    print("\nClassification: Brier score — lower is better")
    print(
        "Constant-probability baseline: "
        f"{brier_score_loss(test['profitable'], constant_probability):.3f}"
    )
    print(
        "Logistic regression: "
        f"{brier_score_loss(test['profitable'], predicted_probability):.3f}"
    )

    # Relate the research to your project's maximum purchase price.
    eligible = (
            (test["ask"] <= 98)
            & (test["ask_quantity"] >= 10)
    )

    print("\nTest-period quote opportunities:")
    describe_selection("Ask <= 98", test, eligible)
    describe_selection(
        "Ask <= 98 and predicted margin > 0",
        test,
        eligible & (predicted_margin > 0),
        )
    describe_selection(
        "Ask <= 98 and predicted positive-margin probability >= 65%",
        test,
        eligible & (predicted_probability >= 0.65),
        )

    print(
        "\nThese are overlapping quote observations, not executed trades. "
        "Do not add their margins together as strategy profit."
    )


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Analyse synthetic quotes exported by the Java project."
    )
    parser.add_argument("quotes", type=Path, help="Path to quotes.csv")
    arguments = parser.parse_args()
    run(arguments.quotes)
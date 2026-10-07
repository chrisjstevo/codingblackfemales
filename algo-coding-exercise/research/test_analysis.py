import unittest

import numpy as np
import pandas as pd

from analysis import FEATURES, prepare_data, split_data


def example_quotes():
    rows = 300
    bid = 100.0 + np.arange(rows)

    return pd.DataFrame({
        "timestamp_ms": np.arange(rows) * 1_000,
        "bid": bid,
        "ask": bid + 2,
        "bid_quantity": np.full(rows, 200),
        "ask_quantity": np.full(rows, 100),
    })


class AnalysisTest(unittest.TestCase):

    def test_margin_uses_future_bid_and_current_ask(self):
        quotes = example_quotes()
        data = prepare_data(quotes)

        # At second 10: ask = 112.
        # At second 40: bid = 140.
        expected = 140 - 112 - 0.40

        self.assertAlmostEqual(data.loc[10, "margin"], expected)
        self.assertEqual(data.loc[10, "profitable"], 1)

    def test_future_prices_do_not_change_current_features(self):
        original = example_quotes()
        changed = original.copy()

        # Change only prices after the observation we are checking.
        changed.loc[20:, ["bid", "ask"]] += 50

        first = prepare_data(original)
        second = prepare_data(changed)

        np.testing.assert_allclose(
            first.loc[10, FEATURES].to_numpy(dtype=float),
            second.loc[10, FEATURES].to_numpy(dtype=float),
        )

        # The future outcome should change, but current inputs should not.
        self.assertNotEqual(
            first.loc[10, "margin"],
            second.loc[10, "margin"],
        )

    def test_training_outcomes_end_before_test_period(self):
        data = prepare_data(example_quotes())
        train, test = split_data(data)

        self.assertLess(
            train["target_timestamp_ms"].max(),
            test["timestamp_ms"].min(),
        )

    def test_crossed_quote_is_rejected(self):
        quotes = example_quotes()
        quotes.loc[5, "ask"] = quotes.loc[5, "bid"] - 1

        with self.assertRaises(ValueError):
            prepare_data(quotes)


if __name__ == "__main__":
    unittest.main()
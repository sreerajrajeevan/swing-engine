package com.sree.swingengine.market.indicator.calculator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class WilderSmoothingCalculator {

    public List<BigDecimal> smooth(List<BigDecimal> values, int period) {

        List<BigDecimal> smoothed = new ArrayList<>();

        if (values == null || values.size() < period) {
            return smoothed;
        }

        // Fill initial values with null
        for (int i = 0; i < period - 1; i++) {
            smoothed.add(null);
        }

        // Initial sum
        BigDecimal sum = BigDecimal.ZERO;

        for (int i = 0; i < period; i++) {
            sum = sum.add(values.get(i));
        }

        smoothed.add(sum);

        // Wilder smoothing
        for (int i = period; i < values.size(); i++) {

            BigDecimal previous = smoothed.get(i - 1);

            BigDecimal current = previous
                    .subtract(previous.divide(BigDecimal.valueOf(period), 10, RoundingMode.HALF_UP))
                    .add(values.get(i));

            smoothed.add(current);
        }

        return smoothed;
    }
}
package com.sree.swingengine.market.indicator.calculator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Component
public class RollingStatisticsCalculator {

    private static final MathContext MC = MathContext.DECIMAL64;

    public BigDecimal calculateSma(
            List<BigDecimal> values,
            int endIndex,
            int period) {

        BigDecimal sum = BigDecimal.ZERO;

        int startIndex = endIndex - period + 1;

        for (int i = startIndex; i <= endIndex; i++) {
            sum = sum.add(values.get(i));
        }

        return sum.divide(
                BigDecimal.valueOf(period),
                10,
                RoundingMode.HALF_UP
        );
    }

    public BigDecimal calculateStandardDeviation(
            List<BigDecimal> values,
            int endIndex,
            int period) {

        BigDecimal mean = calculateSma(values, endIndex, period);

        BigDecimal variance = BigDecimal.ZERO;

        int startIndex = endIndex - period + 1;

        for (int i = startIndex; i <= endIndex; i++) {

            BigDecimal difference =
                    values.get(i).subtract(mean);

            variance = variance.add(
                    difference.multiply(difference)
            );
        }

        variance = variance.divide(
                BigDecimal.valueOf(period),
                10,
                RoundingMode.HALF_UP
        );

        return BigDecimal.valueOf(
                Math.sqrt(variance.doubleValue())
        ).round(MC);
    }
}
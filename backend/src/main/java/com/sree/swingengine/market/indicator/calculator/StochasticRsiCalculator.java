package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.market.indicator.model.StochasticRsiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StochasticRsiCalculator {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final RollingStatisticsCalculator rollingStatisticsCalculator;

    public StochasticRsiResult calculate(
            List<BigDecimal> rsi,
            int rsiPeriod,
            int stochasticPeriod,
            int kPeriod,
            int dPeriod) {

        List<BigDecimal> rawK = new ArrayList<>();

        // Calculate Raw %K
        for (int i = 0; i < rsi.size(); i++) {

            if (i < stochasticPeriod - 1 || rsi.get(i) == null) {
                rawK.add(null);
                continue;
            }

            BigDecimal highest = rsi.get(i);
            BigDecimal lowest = rsi.get(i);

            for (int j = i - stochasticPeriod + 1; j <= i; j++) {

                BigDecimal value = rsi.get(j);

                if (value == null) {
                    continue;
                }

                if (value.compareTo(highest) > 0) {
                    highest = value;
                }

                if (value.compareTo(lowest) < 0) {
                    lowest = value;
                }
            }

            BigDecimal range = highest.subtract(lowest);

            if (range.compareTo(BigDecimal.ZERO) == 0) {
                rawK.add(BigDecimal.ZERO);
                continue;
            }

            BigDecimal k = rsi.get(i)
                    .subtract(lowest)
                    .multiply(HUNDRED)
                    .divide(range, 10, RoundingMode.HALF_UP);

            rawK.add(k);
        }

        List<BigDecimal> k = calculateSma(rawK, kPeriod);

        List<BigDecimal> d = calculateSma(k, dPeriod);

        return StochasticRsiResult.builder()
                .k(k)
                .d(d)
                .build();
    }

    private List<BigDecimal> calculateSma(
            List<BigDecimal> values,
            int period) {

        List<BigDecimal> result = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {

            if (i < period - 1) {
                result.add(null);
                continue;
            }

            boolean hasNull = false;

            for (int j = i - period + 1; j <= i; j++) {
                if (values.get(j) == null) {
                    hasNull = true;
                    break;
                }
            }

            if (hasNull) {
                result.add(null);
                continue;
            }

            result.add(
                    rollingStatisticsCalculator.calculateSma(
                            values,
                            i,
                            period
                    )
            );
        }

        return result;
    }
}
package com.sree.swingengine.market.indicator.calculator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RsiCalculator {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public List<BigDecimal> calculate(List<BigDecimal> closes, int period) {

        List<BigDecimal> changes = calculateChanges(closes);

        List<BigDecimal> gains = calculatePositiveValues(changes);
        List<BigDecimal> losses = calculateNegativeValues(changes);

        List<BigDecimal> averageGains = calculateAverage(gains, period);
        List<BigDecimal> averageLosses = calculateAverage(losses, period);

        return calculateRsi(averageGains, averageLosses);
    }

    private List<BigDecimal> calculateChanges(List<BigDecimal> closes) {

        if (closes == null || closes.isEmpty()) {
            return Collections.emptyList();
        }

        List<BigDecimal> changes = new ArrayList<>();
        changes.add(null);

        for (int i = 1; i < closes.size(); i++) {
            changes.add(closes.get(i).subtract(closes.get(i - 1)));
        }

        return changes;
    }

    private List<BigDecimal> calculatePositiveValues(List<BigDecimal> changes) {

        List<BigDecimal> values = new ArrayList<>();

        for (BigDecimal change : changes) {

            if (change == null) {
                values.add(null);
            } else {
                values.add(change.max(BigDecimal.ZERO));
            }
        }

        return values;
    }

    private List<BigDecimal> calculateNegativeValues(List<BigDecimal> changes) {

        List<BigDecimal> values = new ArrayList<>();

        for (BigDecimal change : changes) {

            if (change == null) {
                values.add(null);
            } else {
                values.add(change.min(BigDecimal.ZERO).abs());
            }
        }

        return values;
    }

    private List<BigDecimal> calculateAverage(
            List<BigDecimal> values,
            int period) {

        List<BigDecimal> averages = new ArrayList<>();

        for (int i = 0; i < period; i++) {
            averages.add(null);
        }

        if (values.size() <= period) {
            return averages;
        }

        BigDecimal sum = BigDecimal.ZERO;

        for (int i = 1; i <= period; i++) {
            sum = sum.add(values.get(i));
        }

        BigDecimal previousAverage = sum.divide(
                BigDecimal.valueOf(period),
                10,
                RoundingMode.HALF_UP);

        averages.add(previousAverage);

        for (int i = period + 1; i < values.size(); i++) {

            previousAverage = previousAverage
                    .multiply(BigDecimal.valueOf(period - 1))
                    .add(values.get(i))
                    .divide(
                            BigDecimal.valueOf(period),
                            10,
                            RoundingMode.HALF_UP);

            averages.add(previousAverage);
        }

        return averages;
    }

    private List<BigDecimal> calculateRsi(
            List<BigDecimal> averageGains,
            List<BigDecimal> averageLosses) {

        List<BigDecimal> rsiValues = new ArrayList<>();

        for (int i = 0; i < averageGains.size(); i++) {

            BigDecimal avgGain = averageGains.get(i);
            BigDecimal avgLoss = averageLosses.get(i);

            if (avgGain == null || avgLoss == null) {
                rsiValues.add(null);
                continue;
            }

            if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
                rsiValues.add(HUNDRED);
                continue;
            }

            BigDecimal rs = avgGain.divide(
                    avgLoss,
                    10,
                    RoundingMode.HALF_UP);

            BigDecimal rsi = HUNDRED.subtract(
                    HUNDRED.divide(
                            BigDecimal.ONE.add(rs),
                            10,
                            RoundingMode.HALF_UP));

            rsiValues.add(rsi.setScale(4, RoundingMode.HALF_UP));
        }

        return rsiValues;
    }
}
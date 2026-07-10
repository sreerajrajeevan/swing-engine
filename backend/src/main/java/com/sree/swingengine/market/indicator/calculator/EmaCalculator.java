package com.sree.swingengine.market.indicator.calculator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class EmaCalculator {

    public List<BigDecimal> calculate(List<BigDecimal> closes, int period) {

        if (closes == null || closes.isEmpty()) {
            return Collections.emptyList();
        }

        List<BigDecimal> emaValues = new ArrayList<>();

        // First (period-1) values have no EMA
        if (closes.size() < period) {

            for (int i = 0; i < closes.size(); i++) {
                emaValues.add(null);
            }

            return emaValues;
        }

        for (int i = 0; i < period - 1; i++) {
            emaValues.add(null);
        }

        // Calculate initial SMA
        BigDecimal sum = BigDecimal.ZERO;

        for (int i = 0; i < period; i++) {
            sum = sum.add(closes.get(i));
        }

        BigDecimal previousEma = sum.divide(
                BigDecimal.valueOf(period),
                4,
                RoundingMode.HALF_UP);

        emaValues.add(previousEma);

        BigDecimal multiplier =
                BigDecimal.valueOf(2.0 / (period + 1));

        // Calculate remaining EMAs
        for (int i = period; i < closes.size(); i++) {

            BigDecimal close = closes.get(i);

            previousEma = close.subtract(previousEma)
                    .multiply(multiplier)
                    .add(previousEma)
                    .setScale(4, RoundingMode.HALF_UP);

            emaValues.add(previousEma);
        }

        return emaValues;
    }
}
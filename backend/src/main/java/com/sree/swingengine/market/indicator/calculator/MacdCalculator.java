package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.market.indicator.model.MacdResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MacdCalculator {

    private static final int FAST_PERIOD = 12;
    private static final int SLOW_PERIOD = 26;
    private static final int SIGNAL_PERIOD = 9;

    private final EmaCalculator emaCalculator;

    public MacdResult calculate(List<BigDecimal> closes) {

        List<BigDecimal> ema12 =
                emaCalculator.calculate(closes, FAST_PERIOD);

        List<BigDecimal> ema26 =
                emaCalculator.calculate(closes, SLOW_PERIOD);

        List<BigDecimal> macd = calculateMacdLine(ema12, ema26);

        List<BigDecimal> signal = calculateSignalLine(macd);

        List<BigDecimal> histogram = calculateHistogram(macd, signal);

        return MacdResult.builder()
                .macd(macd)
                .signal(signal)
                .histogram(histogram)
                .build();
    }

    private List<BigDecimal> calculateMacdLine(
            List<BigDecimal> ema12,
            List<BigDecimal> ema26) {

        List<BigDecimal> macd = new ArrayList<>();

        for (int i = 0; i < ema12.size(); i++) {

            if (ema12.get(i) == null || ema26.get(i) == null) {
                macd.add(null);
                continue;
            }

            macd.add(
                    ema12.get(i)
                            .subtract(ema26.get(i))
                            .setScale(4, RoundingMode.HALF_UP)
            );
        }

        return macd;
    }

    private List<BigDecimal> calculateSignalLine(
            List<BigDecimal> macd) {

        List<BigDecimal> values = new ArrayList<>();

        for (BigDecimal value : macd) {

            if (value != null) {
                values.add(value);
            }
        }

        List<BigDecimal> signalValues =
                emaCalculator.calculate(values, SIGNAL_PERIOD);

        List<BigDecimal> signal = new ArrayList<>();

        int signalIndex = 0;

        for (BigDecimal value : macd) {

            if (value == null) {
                signal.add(null);
            } else {
                signal.add(signalValues.get(signalIndex++));
            }
        }

        return signal;
    }

    private List<BigDecimal> calculateHistogram(
            List<BigDecimal> macd,
            List<BigDecimal> signal) {

        List<BigDecimal> histogram = new ArrayList<>();

        for (int i = 0; i < macd.size(); i++) {

            if (macd.get(i) == null || signal.get(i) == null) {
                histogram.add(null);
                continue;
            }

            histogram.add(
                    macd.get(i)
                            .subtract(signal.get(i))
                            .setScale(4, RoundingMode.HALF_UP)
            );
        }

        return histogram;
    }
}
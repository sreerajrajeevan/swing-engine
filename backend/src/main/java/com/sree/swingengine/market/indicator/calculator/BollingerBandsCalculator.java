package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.market.indicator.model.BollingerBandsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BollingerBandsCalculator {

    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    private final RollingStatisticsCalculator rollingStatisticsCalculator;

    public BollingerBandsResult calculate(List<BigDecimal> closes, int period) {

        List<BigDecimal> upperBand = new ArrayList<>();
        List<BigDecimal> middleBand = new ArrayList<>();
        List<BigDecimal> lowerBand = new ArrayList<>();

        for (int i = 0; i < closes.size(); i++) {

            if (i < period - 1) {
                upperBand.add(null);
                middleBand.add(null);
                lowerBand.add(null);
                continue;
            }

            BigDecimal sma =
                    rollingStatisticsCalculator.calculateSma(closes, i, period);

            BigDecimal standardDeviation =
                    rollingStatisticsCalculator.calculateStandardDeviation(closes, i, period);

            BigDecimal deviation =
                    standardDeviation.multiply(TWO);

            middleBand.add(sma);

            upperBand.add(
                    sma.add(deviation)
                            .setScale(10, RoundingMode.HALF_UP)
            );

            lowerBand.add(
                    sma.subtract(deviation)
                            .setScale(10, RoundingMode.HALF_UP)
            );
        }

        return BollingerBandsResult.builder()
                .upperBand(upperBand)
                .middleBand(middleBand)
                .lowerBand(lowerBand)
                .build();
    }
}
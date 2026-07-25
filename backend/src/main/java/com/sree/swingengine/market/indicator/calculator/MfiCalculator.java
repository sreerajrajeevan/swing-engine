package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.entity.DailyPrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class MfiCalculator {

    private static final BigDecimal THREE = BigDecimal.valueOf(3);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public List<BigDecimal> calculate(List<DailyPrice> prices, int period) {

        List<BigDecimal> typicalPrice = new ArrayList<>();
        List<BigDecimal> rawMoneyFlow = new ArrayList<>();
        List<BigDecimal> positiveMoneyFlow = new ArrayList<>();
        List<BigDecimal> negativeMoneyFlow = new ArrayList<>();
        List<BigDecimal> mfi = new ArrayList<>();

        // Step 1 - Calculate Typical Price
        for (DailyPrice price : prices) {

            BigDecimal tp = price.getHigh()
                    .add(price.getLow())
                    .add(price.getClose())
                    .divide(THREE, 10, RoundingMode.HALF_UP);

            typicalPrice.add(tp);
        }

        // Step 2 - Calculate Raw Money Flow
        for (int i = 0; i < prices.size(); i++) {

            BigDecimal rmf = typicalPrice.get(i)
                    .multiply(BigDecimal.valueOf(prices.get(i).getVolume()));

            rawMoneyFlow.add(rmf);
        }

        // Step 3 - Split into Positive and Negative Money Flow
        positiveMoneyFlow.add(BigDecimal.ZERO);
        negativeMoneyFlow.add(BigDecimal.ZERO);

        for (int i = 1; i < prices.size(); i++) {

            BigDecimal currentTp = typicalPrice.get(i);
            BigDecimal previousTp = typicalPrice.get(i - 1);

            int comparison = currentTp.compareTo(previousTp);

            if (comparison > 0) {

                positiveMoneyFlow.add(rawMoneyFlow.get(i));
                negativeMoneyFlow.add(BigDecimal.ZERO);

            } else if (comparison < 0) {

                positiveMoneyFlow.add(BigDecimal.ZERO);
                negativeMoneyFlow.add(rawMoneyFlow.get(i));

            } else {

                positiveMoneyFlow.add(BigDecimal.ZERO);
                negativeMoneyFlow.add(BigDecimal.ZERO);
            }
        }

        // Warm-up candles
        for (int i = 0; i < period; i++) {
            mfi.add(null);
        }

        // Step 4 & 5 - Calculate MFI
        for (int i = period; i < prices.size(); i++) {

            BigDecimal positiveSum = BigDecimal.ZERO;
            BigDecimal negativeSum = BigDecimal.ZERO;

            for (int j = i - period + 1; j <= i; j++) {
                positiveSum = positiveSum.add(positiveMoneyFlow.get(j));
                negativeSum = negativeSum.add(negativeMoneyFlow.get(j));
            }

            BigDecimal currentMfi;

            if (negativeSum.compareTo(BigDecimal.ZERO) == 0) {

                currentMfi = HUNDRED;

            } else {

                BigDecimal moneyRatio = positiveSum.divide(
                        negativeSum,
                        10,
                        RoundingMode.HALF_UP
                );

                currentMfi = HUNDRED.subtract(
                        HUNDRED.divide(
                                BigDecimal.ONE.add(moneyRatio),
                                10,
                                RoundingMode.HALF_UP
                        )
                );
            }

            mfi.add(currentMfi);
        }

        return mfi;
    }
}
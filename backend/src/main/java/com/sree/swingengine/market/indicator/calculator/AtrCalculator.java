package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.entity.DailyPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AtrCalculator {

    private final TrueRangeCalculator trueRangeCalculator;
    private final WilderSmoothingCalculator wilderSmoothingCalculator;

    public List<BigDecimal> calculate(List<DailyPrice> prices, int period) {

        List<BigDecimal> trueRange =
                trueRangeCalculator.calculate(prices);

        List<BigDecimal> smoothedTrueRange =
                wilderSmoothingCalculator.smooth(trueRange, period);

        List<BigDecimal> atr = new ArrayList<>();

        for (BigDecimal value : smoothedTrueRange) {

            if (value == null) {
                atr.add(null);
                continue;
            }

            atr.add(
                    value.divide(
                            BigDecimal.valueOf(period),
                            10,
                            RoundingMode.HALF_UP
                    )
            );
        }

        return atr;
    }
}
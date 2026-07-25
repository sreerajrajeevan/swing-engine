package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.entity.DailyPrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class VolumeSmaCalculator {

    public List<BigDecimal> calculate(List<DailyPrice> prices, int period) {

        List<BigDecimal> volumeSma = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {

            if (i < period - 1) {
                volumeSma.add(null);
                continue;
            }

            long sum = 0;

            for (int j = i - period + 1; j <= i; j++) {
                sum += prices.get(j).getVolume();
            }

            volumeSma.add(
                    BigDecimal.valueOf(sum)
                            .divide(BigDecimal.valueOf(period), 2, RoundingMode.HALF_UP)
            );
        }

        return volumeSma;
    }
}
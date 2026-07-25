package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.entity.DailyPrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class RelativeVolumeCalculator {

    public List<BigDecimal> calculate(List<DailyPrice> prices,
                                      List<BigDecimal> volumeSma) {

        List<BigDecimal> rvol = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {

            if (volumeSma.get(i) == null
                    || volumeSma.get(i).compareTo(BigDecimal.ZERO) == 0) {

                rvol.add(null);
                continue;
            }

            rvol.add(
                    BigDecimal.valueOf(prices.get(i).getVolume())
                            .divide(volumeSma.get(i), 2, RoundingMode.HALF_UP)
            );
        }

        return rvol;
    }
}
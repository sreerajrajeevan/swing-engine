package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.entity.DailyPrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class TrueRangeCalculator {

    public List<BigDecimal> calculate(List<DailyPrice> prices) {

        if (prices == null || prices.isEmpty()) {
            return List.of();
        }

        List<BigDecimal> trueRanges = new ArrayList<>(prices.size());

        // First candle
        DailyPrice firstPrice = prices.get(0);
        trueRanges.add(
                firstPrice.getHigh().subtract(firstPrice.getLow())
        );

        // Remaining candles
        for (int i = 1; i < prices.size(); i++) {

            DailyPrice current = prices.get(i);
            DailyPrice previous = prices.get(i - 1);

            BigDecimal highLow =
                    current.getHigh().subtract(current.getLow());

            BigDecimal highPreviousClose =
                    current.getHigh()
                            .subtract(previous.getClose())
                            .abs();

            BigDecimal lowPreviousClose =
                    current.getLow()
                            .subtract(previous.getClose())
                            .abs();

            BigDecimal trueRange = highLow
                    .max(highPreviousClose)
                    .max(lowPreviousClose);

            trueRanges.add(trueRange);
        }

        return trueRanges;
    }
}
package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.entity.DailyPrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ObvCalculator {

    public List<BigDecimal> calculate(List<DailyPrice> prices) {

        List<BigDecimal> obv = new ArrayList<>();

        if (prices == null || prices.isEmpty()) {
            return obv;
        }

        BigDecimal currentObv = BigDecimal.ZERO;
        obv.add(currentObv);

        for (int i = 1; i < prices.size(); i++) {

            BigDecimal currentClose = prices.get(i).getClose();
            BigDecimal previousClose = prices.get(i - 1).getClose();

            BigDecimal volume = BigDecimal.valueOf(prices.get(i).getVolume());

            if (currentClose.compareTo(previousClose) > 0) {
                currentObv = currentObv.add(volume);
            } else if (currentClose.compareTo(previousClose) < 0) {
                currentObv = currentObv.subtract(volume);
            }
            // If equal, OBV remains unchanged

            obv.add(currentObv);
        }

        return obv;
    }
}
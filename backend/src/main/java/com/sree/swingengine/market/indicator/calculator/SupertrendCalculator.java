package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.market.indicator.model.SupertrendResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SupertrendCalculator {

    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    private final AtrCalculator atrCalculator;

    public SupertrendResult calculate(
            List<DailyPrice> prices,
            int period,
            BigDecimal multiplier) {

        List<BigDecimal> atr =
                atrCalculator.calculate(prices, period);

// Print last 20 ATR values
        for (int i = Math.max(0, atr.size() - 20); i < atr.size(); i++) {
            if (atr.get(i) != null) {
                System.out.println(
                        prices.get(i).getTradingDate()
                                + " ATR=" + atr.get(i)
                );
            }
        }

        List<BigDecimal> basicUpperBand = new ArrayList<>();
        List<BigDecimal> basicLowerBand = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {

            if (atr.get(i) == null) {
                basicUpperBand.add(null);
                basicLowerBand.add(null);
                continue;
            }

            DailyPrice price = prices.get(i);

            BigDecimal hl2 = price.getHigh()
                    .add(price.getLow())
                    .divide(TWO, 10, RoundingMode.HALF_UP);

            BigDecimal atrMultiplier =
                    atr.get(i).multiply(multiplier);

            basicUpperBand.add(
                    hl2.add(atrMultiplier)
            );

            basicLowerBand.add(
                    hl2.subtract(atrMultiplier)
            );
        }
        System.out.println("Basic Upper Last : "
                + basicUpperBand.get(basicUpperBand.size() - 1));

        System.out.println("Basic Lower Last : "
                + basicLowerBand.get(basicLowerBand.size() - 1));

        // Final bands and trend logic will be added next.
        List<BigDecimal> finalUpperBand = new ArrayList<>();
        List<BigDecimal> finalLowerBand = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {

            if (basicUpperBand.get(i) == null) {
                finalUpperBand.add(null);
                finalLowerBand.add(null);
                continue;
            }

            if (i == 0 || finalUpperBand.isEmpty() || finalUpperBand.get(i - 1) == null) {

                finalUpperBand.add(basicUpperBand.get(i));
                finalLowerBand.add(basicLowerBand.get(i));
                continue;
            }

            BigDecimal previousFinalUpper = finalUpperBand.get(i - 1);
            BigDecimal previousFinalLower = finalLowerBand.get(i - 1);
            BigDecimal previousClose = prices.get(i - 1).getClose();

            BigDecimal currentBasicUpper = basicUpperBand.get(i);
            BigDecimal currentBasicLower = basicLowerBand.get(i);

            // Final Upper Band
            if (currentBasicUpper.compareTo(previousFinalUpper) < 0
                    || previousClose.compareTo(previousFinalUpper) > 0) {

                finalUpperBand.add(currentBasicUpper);

            } else {

                finalUpperBand.add(previousFinalUpper);
            }

            // Final Lower Band
            if (currentBasicLower.compareTo(previousFinalLower) > 0
                    || previousClose.compareTo(previousFinalLower) < 0) {

                finalLowerBand.add(currentBasicLower);

            } else {

                finalLowerBand.add(previousFinalLower);
            }
        }

        System.out.println("Final Upper Last : "
                + finalUpperBand.get(finalUpperBand.size() - 1));

        System.out.println("Final Lower Last : "
                + finalLowerBand.get(finalLowerBand.size() - 1));

        List<BigDecimal> supertrend = new ArrayList<>();
        List<String> direction = new ArrayList<>();
        boolean isUpTrend = false;

        for (int i = 0; i < prices.size(); i++) {

            if (finalUpperBand.get(i) == null) {
                supertrend.add(null);
                direction.add(null);
                continue;
            }

            if (i == 0 || supertrend.get(i - 1) == null) {

                supertrend.add(finalUpperBand.get(i));
                direction.add("SELL");
                isUpTrend = false;
                continue;
            }

            BigDecimal currentUpper = finalUpperBand.get(i);
            BigDecimal currentLower = finalLowerBand.get(i);
            BigDecimal close = prices.get(i).getClose();

            if (!isUpTrend) {

                // Down trend
                if (close.compareTo(currentUpper) > 0) {
                    isUpTrend = true;
                }

            } else {

                // Up trend
                if (close.compareTo(currentLower) < 0) {
                    isUpTrend = false;
                }
            }

            if (isUpTrend) {
                supertrend.add(currentLower);
                direction.add("BUY");
            } else {
                supertrend.add(currentUpper);
                direction.add("SELL");
            }

            System.out.println(
                    prices.get(i).getTradingDate()
                            + " Close=" + close
                            + " Upper=" + currentUpper
                            + " Lower=" + currentLower
                            + " Trend=" + (isUpTrend ? "BUY" : "SELL")
                            + " ST=" + supertrend.get(i)
            );
            System.out.println(
                    prices.get(i).getTradingDate()
                            + " Close=" + prices.get(i).getClose()
                            + " BU=" + basicUpperBand.get(i)
                            + " BL=" + basicLowerBand.get(i)
                            + " FU=" + finalUpperBand.get(i)
                            + " FL=" + finalLowerBand.get(i)
            );
        }

        System.out.println("Supertrend Last : "
                + supertrend.get(supertrend.size() - 1));

        System.out.println("Direction Last  : "
                + direction.get(direction.size() - 1));

        return SupertrendResult.builder()
                .supertrend(supertrend)
                .direction(direction)
                .build();


    }
}
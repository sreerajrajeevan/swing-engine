package com.sree.swingengine.market.indicator.calculator;

import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.market.indicator.model.AdxResult;
import com.sree.swingengine.market.indicator.model.DirectionalMovement;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Component
public class AdxCalculator {

    private final TrueRangeCalculator trueRangeCalculator;
    public AdxCalculator(TrueRangeCalculator trueRangeCalculator) {
        this.trueRangeCalculator = trueRangeCalculator;
    }

    public AdxResult calculate(List<DailyPrice> prices, int period) {

        List<BigDecimal> trueRange =
                trueRangeCalculator.calculate(prices);

        DirectionalMovement directionalMovement =
                calculateDirectionalMovement(prices);

        List<BigDecimal> smoothedTrueRange =
                smooth(trueRange, period);

        List<BigDecimal> smoothedPlusDm =
                smooth(directionalMovement.getPlusDm(), period);

        List<BigDecimal> smoothedMinusDm =
                smooth(directionalMovement.getMinusDm(), period);

        List<BigDecimal> plusDi =
                calculateDi(smoothedPlusDm, smoothedTrueRange);

        List<BigDecimal> minusDi =
                calculateDi(smoothedMinusDm, smoothedTrueRange);

        List<BigDecimal> dx =
                calculateDx(plusDi, minusDi);

        List<BigDecimal> adx =
                calculateAdx(dx, period);

        return AdxResult.builder()
                .adx(adx)
                .plusDi(plusDi)
                .minusDi(minusDi)
                .build();
    }


    private DirectionalMovement calculateDirectionalMovement(List<DailyPrice> prices) {

        List<BigDecimal> plusDm = new ArrayList<>();
        List<BigDecimal> minusDm = new ArrayList<>();

        if (prices.isEmpty()) {
            return new DirectionalMovement(plusDm, minusDm);
        }

        plusDm.add(null);
        minusDm.add(null);

        for (int i = 1; i < prices.size(); i++) {

            DailyPrice current = prices.get(i);
            DailyPrice previous = prices.get(i - 1);

            BigDecimal upMove =
                    current.getHigh().subtract(previous.getHigh());

            BigDecimal downMove =
                    previous.getLow().subtract(current.getLow());

            if (upMove.compareTo(downMove) > 0 &&
                    upMove.compareTo(BigDecimal.ZERO) > 0) {

                plusDm.add(upMove);
            } else {
                plusDm.add(BigDecimal.ZERO);
            }

            if (downMove.compareTo(upMove) > 0 &&
                    downMove.compareTo(BigDecimal.ZERO) > 0) {

                minusDm.add(downMove);
            } else {
                minusDm.add(BigDecimal.ZERO);
            }
        }

        return new DirectionalMovement(
                plusDm,
                minusDm
        );
    }

    private List<BigDecimal> smooth(List<BigDecimal> values, int period) {

        List<BigDecimal> smoothed = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            smoothed.add(null);
        }

        if (values.size() <= period) {
            return smoothed;
        }

        BigDecimal firstValue = BigDecimal.ZERO;

        for (int i = 1; i <= period; i++) {
            firstValue = firstValue.add(values.get(i));
        }

        smoothed.set(period, firstValue);

        for (int i = period + 1; i < values.size(); i++) {

            BigDecimal previous = smoothed.get(i - 1);

            BigDecimal current = values.get(i);

            BigDecimal smoothedValue = previous
                    .subtract(previous.divide(
                            BigDecimal.valueOf(period),
                            10,
                            RoundingMode.HALF_UP))
                    .add(current);

            smoothed.set(i, smoothedValue);
        }

        return smoothed;
    }

    private List<BigDecimal> calculateDi(
            List<BigDecimal> smoothedDm,
            List<BigDecimal> smoothedTrueRange) {

        List<BigDecimal> di = new ArrayList<>();

        for (int i = 0; i < smoothedDm.size(); i++) {

            BigDecimal dm = smoothedDm.get(i);
            BigDecimal tr = smoothedTrueRange.get(i);

            if (dm == null || tr == null || tr.compareTo(BigDecimal.ZERO) == 0) {
                di.add(null);
                continue;
            }

            di.add(
                    dm.multiply(BigDecimal.valueOf(100))
                            .divide(tr, 10, RoundingMode.HALF_UP)
            );
        }

        return di;
    }

    private List<BigDecimal> calculateDx(
            List<BigDecimal> plusDi,
            List<BigDecimal> minusDi) {

        List<BigDecimal> dx = new ArrayList<>();

        for (int i = 0; i < plusDi.size(); i++) {

            BigDecimal plus = plusDi.get(i);
            BigDecimal minus = minusDi.get(i);

            if (plus == null || minus == null) {
                dx.add(null);
                continue;
            }

            BigDecimal denominator = plus.add(minus);

            if (denominator.compareTo(BigDecimal.ZERO) == 0) {
                dx.add(null);
                continue;
            }

            BigDecimal numerator = plus.subtract(minus).abs();

            dx.add(
                    numerator.multiply(BigDecimal.valueOf(100))
                            .divide(denominator, 10, RoundingMode.HALF_UP)
            );
        }

        return dx;
    }

    private List<BigDecimal> calculateAdx(
            List<BigDecimal> dx,
            int period) {

        List<BigDecimal> adx = new ArrayList<>();

        for (int i = 0; i < dx.size(); i++) {
            adx.add(null);
        }

        if (dx.size() < (period * 2)) {
            return adx;
        }

        BigDecimal firstAdx = BigDecimal.ZERO;

        for (int i = period; i < period * 2; i++) {

            if (dx.get(i) != null) {
                firstAdx = firstAdx.add(dx.get(i));
            }
        }

        firstAdx = firstAdx.divide(
                BigDecimal.valueOf(period),
                10,
                RoundingMode.HALF_UP);

        adx.set(period * 2 - 1, firstAdx);

        for (int i = period * 2; i < dx.size(); i++) {

            if (dx.get(i) == null) {
                continue;
            }

            BigDecimal previousAdx = adx.get(i - 1);

            BigDecimal currentAdx = previousAdx
                    .multiply(BigDecimal.valueOf(period - 1))
                    .add(dx.get(i))
                    .divide(BigDecimal.valueOf(period),
                            10,
                            RoundingMode.HALF_UP);

            adx.set(i, currentAdx);
        }

        return adx;
    }
}
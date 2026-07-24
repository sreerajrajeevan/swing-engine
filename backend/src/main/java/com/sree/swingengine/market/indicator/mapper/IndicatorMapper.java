package com.sree.swingengine.market.indicator.mapper;

import com.sree.swingengine.entity.DailyIndicator;
import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.entity.Stock;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class IndicatorMapper {

    public DailyIndicator toEntity(
            Stock stock,
            DailyPrice price,
            BigDecimal ema20,
            BigDecimal ema50,
            BigDecimal ema200,
            BigDecimal rsi14,
            BigDecimal macd,
            BigDecimal macdSignal,
            BigDecimal macdHistogram,
            BigDecimal adx,
            BigDecimal plusDi,
            BigDecimal minusDi,
            BigDecimal atr14,
            BigDecimal bbUpper,
            BigDecimal bbMiddle,
            BigDecimal bbLower,
            BigDecimal stochasticK,
            BigDecimal stochasticD) {

        return DailyIndicator.builder()
                .stock(stock)
                .tradingDate(price.getTradingDate())
                .ema20(ema20)
                .ema50(ema50)
                .ema200(ema200)
                .rsi14(rsi14)
                .macd(macd)
                .macdSignal(macdSignal)
                .macdHistogram(macdHistogram)
                .adx(adx)
                .plusDi(plusDi)
                .minusDi(minusDi)
                .atr14(atr14)
                .bbUpper(bbUpper)
                .bbMiddle(bbMiddle)
                .bbLower(bbLower)
                .stochasticK(stochasticK)
                .stochasticD(stochasticD)
                .build();
    }
}
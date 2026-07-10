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
            BigDecimal ema200) {

        return DailyIndicator.builder()
                .stock(stock)
                .tradingDate(price.getTradingDate())
                .ema20(ema20)
                .ema50(ema50)
                .ema200(ema200)
                .build();
    }
}
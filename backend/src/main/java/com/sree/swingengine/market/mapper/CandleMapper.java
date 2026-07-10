package com.sree.swingengine.market.mapper;

import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.entity.Stock;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class CandleMapper {

    public DailyPrice toEntity(Stock stock, List<Object> candle) {

        DailyPrice dailyPrice = new DailyPrice();

        dailyPrice.setStock(stock);
        dailyPrice.setTradingDate(
                OffsetDateTime.parse((String) candle.get(0)).toLocalDate()
        );

        dailyPrice.setOpen(new BigDecimal(candle.get(1).toString()));
        dailyPrice.setHigh(new BigDecimal(candle.get(2).toString()));
        dailyPrice.setLow(new BigDecimal(candle.get(3).toString()));
        dailyPrice.setClose(new BigDecimal(candle.get(4).toString()));
        dailyPrice.setVolume(Long.parseLong(candle.get(5).toString()));

        return dailyPrice;
    }
}
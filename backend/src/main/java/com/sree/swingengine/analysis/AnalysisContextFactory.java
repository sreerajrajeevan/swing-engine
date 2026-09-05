package com.sree.swingengine.analysis;

import com.sree.swingengine.analysis.common.AnalysisContext;
import com.sree.swingengine.entity.DailyIndicator;
import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.entity.Stock;
import com.sree.swingengine.repository.DailyIndicatorRepository;
import com.sree.swingengine.repository.DailyPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnalysisContextFactory {

    private final DailyPriceRepository dailyPriceRepository;
    private final DailyIndicatorRepository dailyIndicatorRepository;

    public AnalysisContext build(Stock stock) {

        List<DailyPrice> prices =
                dailyPriceRepository.findByStockOrderByTradingDateAsc(stock);

        List<DailyIndicator> indicators =
                dailyIndicatorRepository.findByStockOrderByTradingDateAsc(stock);

        if (prices.isEmpty()) {
            throw new IllegalStateException(
                    "No DailyPrice found for " + stock.getSymbol());
        }

        if (indicators.isEmpty()) {
            throw new IllegalStateException(
                    "No DailyIndicator found for " + stock.getSymbol());
        }

        return AnalysisContext.builder()
                .stock(stock)
                .currentPrice(prices.get(prices.size() - 1))
                .currentIndicator(indicators.get(indicators.size() - 1))
                .priceHistory(prices)
                .indicatorHistory(indicators)
                .build();
    }
}
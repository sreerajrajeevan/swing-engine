package com.sree.swingengine.market.indicator;

import com.sree.swingengine.entity.DailyIndicator;
import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.entity.Stock;
import com.sree.swingengine.market.indicator.calculator.EmaCalculator;
import com.sree.swingengine.market.indicator.calculator.RsiCalculator;
import com.sree.swingengine.market.indicator.mapper.IndicatorMapper;
import com.sree.swingengine.repository.DailyIndicatorRepository;
import com.sree.swingengine.repository.DailyPriceRepository;
import com.sree.swingengine.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorService {

    private final StockRepository stockRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final DailyIndicatorRepository dailyIndicatorRepository;
    private final EmaCalculator emaCalculator;
    private final RsiCalculator rsiCalculator;
    private final IndicatorMapper indicatorMapper;

    public void calculateIndicators() {

        long startTime = System.currentTimeMillis();

        List<Stock> stocks = stockRepository.findAll();

        int success = 0;
        int failed = 0;

        for (int i = 0; i < stocks.size(); i++) {

            Stock stock = stocks.get(i);

            log.info("[{}/{}] Calculating indicators for {}",
                    i + 1,
                    stocks.size(),
                    stock.getSymbol());

            try {

                calculateStock(stock);
                success++;

            } catch (Exception ex) {

                failed++;

                log.error("[{}/{}] Failed for {}",
                        i + 1,
                        stocks.size(),
                        stock.getSymbol(),
                        ex);
            }
        }

        long elapsedMillis = System.currentTimeMillis() - startTime;

        long minutes = elapsedMillis / 60000;
        long seconds = (elapsedMillis % 60000) / 1000;

        log.info("====================================");
        log.info("Indicator Calculation Completed");
        log.info("Success : {}", success);
        log.info("Failed  : {}", failed);
        log.info("Time Taken : {} min {} sec", minutes, seconds);
        log.info("====================================");
    }

    private void calculateStock(Stock stock) {

        List<DailyPrice> prices =
                dailyPriceRepository.findByStockOrderByTradingDateAsc(stock);

        if (prices.isEmpty()) {
            log.warn("No price data found for {}", stock.getSymbol());
            return;
        }

        List<BigDecimal> closes = prices.stream()
                .map(DailyPrice::getClose)
                .toList();

        List<BigDecimal> ema20 =
                emaCalculator.calculate(closes, 20);

        List<BigDecimal> ema50 =
                emaCalculator.calculate(closes, 50);

        List<BigDecimal> ema200 =
                emaCalculator.calculate(closes, 200);

        // Temporary calculation to keep RSI validated.
        // It will be persisted in the next step.
        rsiCalculator.calculate(closes, 14);

        List<DailyIndicator> indicators = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {

            indicators.add(
                    indicatorMapper.toEntity(
                            stock,
                            prices.get(i),
                            ema20.get(i),
                            ema50.get(i),
                            ema200.get(i)
                    )
            );
        }

        Optional<DailyIndicator> latestIndicator =
                dailyIndicatorRepository.findTopByStockOrderByTradingDateDesc(stock);

        if (latestIndicator.isPresent()) {

            LocalDate latestTradingDate =
                    latestIndicator.get().getTradingDate();

            indicators = indicators.stream()
                    .filter(i -> i.getTradingDate().isAfter(latestTradingDate))
                    .toList();
        }

        if (indicators.isEmpty()) {
            log.info("{} indicators are already up to date.", stock.getSymbol());
            return;
        }

        dailyIndicatorRepository.saveAll(indicators);

        log.info("{} - Saved {} indicators",
                stock.getSymbol(),
                indicators.size());
    }
}
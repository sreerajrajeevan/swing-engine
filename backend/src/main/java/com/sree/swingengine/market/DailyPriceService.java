package com.sree.swingengine.market;

import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.entity.Stock;
import com.sree.swingengine.market.client.angel.AngelOneClient;
import com.sree.swingengine.market.client.angel.dto.AngelHistoricalCandleResponse;
import com.sree.swingengine.market.mapper.CandleMapper;
import com.sree.swingengine.repository.DailyPriceRepository;
import com.sree.swingengine.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyPriceService {

    private final AngelOneClient angelOneClient;
    private final StockRepository stockRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final CandleMapper candleMapper;

    public void loadDailyPrices(boolean fullSync) {

        long startTime = System.currentTimeMillis();

        List<Stock> stocks = stockRepository.findAll();

        int success = 0;
        int failed = 0;

        for (int i = 0; i < stocks.size(); i++) {

            Stock stock = stocks.get(i);

            log.info("[{}/{}] Syncing {}",
                    i + 1,
                    stocks.size(),
                    stock.getSymbol());

            try {

                syncStock(stock, fullSync);
                success++;

            } catch (Exception ex) {

                failed++;

                log.error("[{}/{}] Failed to sync {}",
                        i + 1,
                        stocks.size(),
                        stock.getSymbol(),
                        ex);
            }

            if (i < stocks.size() - 1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.warn("Sync interrupted.");
                    break;
                }
            }
        }

        long elapsedMillis = System.currentTimeMillis() - startTime;

        long minutes = elapsedMillis / 60000;
        long seconds = (elapsedMillis % 60000) / 1000;

        log.info("====================================");
        log.info("Sync Completed");
        log.info("Success : {}", success);
        log.info("Failed  : {}", failed);
        log.info("Time Taken : {} min {} sec", minutes, seconds);
        log.info("====================================");
    }

    private void syncStock(Stock stock, boolean fullSync) {

        LocalDate fromDate;

        if (fullSync) {

            fromDate = LocalDate.now().minusYears(2);

        } else {

            Optional<DailyPrice> latestPrice =
                    dailyPriceRepository.findTopByStockOrderByTradingDateDesc(stock);

            if (latestPrice.isPresent()) {
                fromDate = latestPrice.get()
                        .getTradingDate()
                        .plusDays(1);
            } else {
                fromDate = LocalDate.now().minusYears(5);
            }
        }

        LocalDate toDate = LocalDate.now();

        if (fromDate.isAfter(toDate)) {
            log.info("{} is already up to date.", stock.getSymbol());
            return;
        }

        AngelHistoricalCandleResponse response =
                angelOneClient.downloadHistoricalCandles(
                        stock.getSymbolToken(),
                        fromDate,
                        toDate);

        if (response.getData() == null || response.getData().isEmpty()) {
            log.info("No candles found for {}", stock.getSymbol());
            return;
        }

        List<DailyPrice> prices = response.getData().stream()
                .map(candle -> candleMapper.toEntity(stock, candle))
                .toList();

        dailyPriceRepository.saveAll(prices);

        log.info("{} - Saved {} candles",
                stock.getSymbol(),
                prices.size());
    }
}
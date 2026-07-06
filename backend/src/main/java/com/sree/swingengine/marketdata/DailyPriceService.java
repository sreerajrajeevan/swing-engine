package com.sree.swingengine.marketdata;

import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.entity.Stock;
import com.sree.swingengine.marketdata.client.angel.AngelOneClient;
import com.sree.swingengine.marketdata.client.angel.dto.AngelHistoricalCandleResponse;
import com.sree.swingengine.marketdata.mapper.CandleMapper;
import com.sree.swingengine.repository.DailyPriceRepository;
import com.sree.swingengine.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyPriceService {

    private final AngelOneClient angelOneClient;
    private final StockRepository stockRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final CandleMapper candleMapper;

    public void loadDailyPrices() {

        Stock stock = stockRepository.findBySymbol("SBIN")
                .orElseThrow();

        AngelHistoricalCandleResponse response =
                angelOneClient.downloadHistoricalCandles(
                        stock.getSymbolToken(),
                        LocalDate.now().minusDays(30),
                        LocalDate.now());

        log.info("Status  : {}", response.isStatus());
        log.info("Message : {}", response.getMessage());
        log.info("Data    : {}", response.getData());
        List<DailyPrice> prices = response.getData().stream()
                .map(candle -> candleMapper.toEntity(stock, candle))
                .toList();
        dailyPriceRepository.saveAll(prices);

        log.info("Saved {} candles", prices.size());

        log.info("Downloaded {} candles", prices.size());

        prices.stream()
                .limit(3)
                .forEach(p -> log.info(
                        "{} O:{} H:{} L:{} C:{} V:{}",
                        p.getTradingDate(),
                        p.getOpen(),
                        p.getHigh(),
                        p.getLow(),
                        p.getClose(),
                        p.getVolume()
                ));
    }
}
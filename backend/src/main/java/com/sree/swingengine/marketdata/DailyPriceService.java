package com.sree.swingengine.marketdata;

import com.sree.swingengine.marketdata.client.MarketDataClient;
import com.sree.swingengine.marketdata.client.YahooFinanceClient;
import com.sree.swingengine.repository.DailyPriceRepository;
import com.sree.swingengine.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyPriceService {

    private final MarketDataClient marketDataClient;
    private final YahooFinanceClient yahooFinanceClient;
    private final StockRepository stockRepository;
    private final DailyPriceRepository dailyPriceRepository;
    public void loadDailyPrices() {

        String json = yahooFinanceClient.downloadHistory("SBIN");

        log.info(json.substring(0, Math.min(json.length(), 500)));

    }

}
package com.sree.swingengine.marketdata.client;

import com.sree.swingengine.config.YahooProperties;
import com.sree.swingengine.dto.DailyPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class YahooFinanceClient {
    private final RestClient restClient;
    private final YahooProperties properties;
    public String downloadHistory(String symbol) {

        String url = properties.getBaseUrl()
                + properties.getChartEndpoint()
                + symbol
                + ".NS?range=2y&interval=1d";

        log.info("Calling Yahoo : {}", url);

        return restClient
                .get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }

}
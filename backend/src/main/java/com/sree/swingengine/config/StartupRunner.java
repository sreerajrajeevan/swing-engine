package com.sree.swingengine.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sree.swingengine.marketdata.AngelInstrumentService;
import com.sree.swingengine.marketdata.DailyPriceService;
import com.sree.swingengine.marketdata.MasterStockService;
import com.sree.swingengine.marketdata.client.angel.AngelOneClient;
import com.sree.swingengine.marketdata.client.angel.dto.AngelInstrumentDto;
import com.sree.swingengine.marketdata.client.angel.dto.AngelLoginResponse;
import com.sree.swingengine.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupRunner implements CommandLineRunner {
    private final DailyPriceService dailyPriceService;
    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);
    private final MasterStockService masterStockService;
    private final ApplicationProperties applicationProperties;
    private final AngelOneClient angelOneClient;
    private final AngelInstrumentService angelInstrumentService;
    public StartupRunner(ApplicationProperties applicationProperties,
                         MasterStockService masterStockService,
                         DailyPriceService dailyPriceService,
                         AngelOneClient angelOneClient,
                         AngelInstrumentService angelInstrumentService) {
        this.applicationProperties = applicationProperties;
        this.masterStockService = masterStockService;
        this.dailyPriceService = dailyPriceService;
        this.angelOneClient = angelOneClient;
        this.angelInstrumentService = angelInstrumentService;
    }

    @Override
    public void run(String... args) {

        switch (applicationProperties.getMode()) {

            case NONE -> log.info("Application started in NONE mode.");

            case LOAD_NIFTY500 -> {
                log.info("Loading Nifty 500...");
                masterStockService.loadNifty500();
            }

            case SMART_API_LOGIN -> {
                AngelLoginResponse response = angelOneClient.login();
                log.info("Status  : {}", response.getStatus());
                log.info("Message : {}", response.getMessage());
            }

            case SYNC_SYMBOL_TOKENS -> {
                log.info("Syncing Angel One symbol tokens...");
                try {
                    angelInstrumentService.syncSymbolTokens();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            case LOAD_DAILY_PRICES -> {
                log.info("Loading Daily Prices...");
                dailyPriceService.loadDailyPrices();
            }

            case LOAD_FUNDAMENTALS -> {
                log.info("LOAD_FUNDAMENTALS is not implemented yet.");
            }
        }
    }
}
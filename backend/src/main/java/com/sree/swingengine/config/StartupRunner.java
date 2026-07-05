package com.sree.swingengine.config;

import com.sree.swingengine.marketdata.DailyPriceService;
import com.sree.swingengine.marketdata.MasterStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    private final DailyPriceService dailyPriceService;
    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);
    private final MasterStockService masterStockService;
    private final ApplicationProperties applicationProperties;
    public StartupRunner(ApplicationProperties applicationProperties,
                         MasterStockService masterStockService,
                         DailyPriceService dailyPriceService) {
        this.applicationProperties = applicationProperties;
        this.masterStockService = masterStockService;
        this.dailyPriceService = dailyPriceService;
    }
    @Override
    public void run(String... args) {

        switch (applicationProperties.getMode()) {

            case NONE -> log.info("Application started in NONE mode.");

            case LOAD_NIFTY500 -> {
                log.info("Loading Nifty 500...");
                masterStockService.loadNifty500();
            }

            case LOAD_DAILY_PRICES -> {
                log.info("Loading Daily Prices...");
                dailyPriceService.loadDailyPrices();
            }

            case LOAD_FUNDAMENTALS ->
                    log.info("LOAD_FUNDAMENTALS is not implemented yet.");
        }

    }
}
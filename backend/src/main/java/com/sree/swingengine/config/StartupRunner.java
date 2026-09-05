package com.sree.swingengine.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sree.swingengine.analysis.AnalysisService;
import com.sree.swingengine.market.AngelInstrumentService;
import com.sree.swingengine.market.DailyPriceService;
import com.sree.swingengine.market.MasterStockService;
import com.sree.swingengine.market.indicator.IndicatorService;
import com.sree.swingengine.market.session.AngelSessionManager;
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
    private final AngelSessionManager angelSessionManager;
    private final AngelInstrumentService angelInstrumentService;
    private final IndicatorService indicatorService;
    private final AnalysisService analysisService;
    public StartupRunner(ApplicationProperties applicationProperties,
                         MasterStockService masterStockService,
                         DailyPriceService dailyPriceService,
                         AngelSessionManager angelSessionManager,
                         AngelInstrumentService angelInstrumentService,
                         IndicatorService indicatorService,
                         AnalysisService analysisService) {
        this.applicationProperties = applicationProperties;
        this.masterStockService = masterStockService;
        this.dailyPriceService = dailyPriceService;
        this.angelSessionManager = angelSessionManager;
        this.angelInstrumentService = angelInstrumentService;
        this.indicatorService = indicatorService;
        this.analysisService = analysisService;
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

                String jwt = angelSessionManager.getJwtToken();

                log.info("JWT Token generated successfully.");
                log.info("Token starts with: {}", jwt.substring(0, 20) + "...");
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
                dailyPriceService.loadDailyPrices(false);
            }
            case CALCULATE_INDICATORS -> {

                log.info("Calculating Indicators...");

                indicatorService.calculateIndicators();

            }

            case LOAD_FUNDAMENTALS -> {
                log.info("LOAD_FUNDAMENTALS is not implemented yet.");
            }
            case ANALYZE_STOCKS -> {

                log.info("Analyzing Stocks...");

                analysisService.analyzeStocks();

            }
        }
    }
}
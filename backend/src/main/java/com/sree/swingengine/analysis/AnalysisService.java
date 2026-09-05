package com.sree.swingengine.analysis;

import com.sree.swingengine.analysis.common.AnalysisContext;
import com.sree.swingengine.analysis.model.StockAnalysisResult;
import com.sree.swingengine.analysis.trend.TrendAnalysisResult;
import com.sree.swingengine.analysis.trend.TrendAnalyzer;
import com.sree.swingengine.entity.Stock;
import com.sree.swingengine.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private static final int MINIMUM_SCORE = 70;

    private final StockRepository stockRepository;
    private final AnalysisContextFactory contextFactory;
    private final TrendAnalyzer trendAnalyzer;

    public void analyzeStocks() {

        List<Stock> stocks = stockRepository.findAll();

        List<StockAnalysisResult> shortlisted = new ArrayList<>();

        int success = 0;
        int failed = 0;

        long start = System.currentTimeMillis();

        for (Stock stock : stocks) {

            try {

                TrendAnalysisResult trend = analyze(stock);

                success++;

                if (trend.getScore() >= MINIMUM_SCORE) {

                    shortlisted.add(
                            StockAnalysisResult.builder()
                                    .stock(stock)
                                    .trend(trend)
                                    .build()
                    );
                }

            } catch (Exception ex) {

                failed++;

                log.error("Failed to analyze stock: {}", stock.getSymbol(), ex);
            }
        }

        shortlisted.sort(
                Comparator.comparingInt(
                                (StockAnalysisResult r) -> r.getTrend().getScore())
                        .reversed()
        );

        printSummary(shortlisted, stocks.size(), success, failed, start);
    }

    private TrendAnalysisResult analyze(Stock stock) {

        AnalysisContext context = contextFactory.build(stock);

        return trendAnalyzer.analyze(context);
    }

    private void printSummary(List<StockAnalysisResult> shortlisted,
                              int totalStocks,
                              int success,
                              int failed,
                              long startTime) {

        long timeTaken = System.currentTimeMillis() - startTime;

        log.info("");
        log.info("==============================================================");
        log.info("               SWING ENGINE - TREND ANALYSIS");
        log.info("==============================================================");
        log.info("");

        if (shortlisted.isEmpty()) {

            log.info("No qualifying stocks found.");

        } else {

            log.info(String.format("%-5s %-20s %-8s %-15s",
                    "Rank",
                    "Symbol",
                    "Score",
                    "Strength"));

            log.info("--------------------------------------------------------------");

            int rank = 1;

            for (StockAnalysisResult result : shortlisted) {

                log.info(String.format("%-5d %-20s %-8d %-15s",
                        rank++,
                        result.getStock().getSymbol(),
                        result.getTrend().getScore(),
                        result.getTrend().getStrength()));

                result.getTrend().getScoreBreakdown()
                        .forEach((key, value) ->
                                log.info("      {} : {}", key, value));

                log.info("");
            }
        }

        log.info("");
        log.info("--------------------------------------------------------------");
        log.info("Total Stocks      : {}", totalStocks);
        log.info("Qualified Stocks  : {}", shortlisted.size());
        log.info("Success           : {}", success);
        log.info("Failed            : {}", failed);
        log.info("Time Taken        : {} ms", timeTaken);
        log.info("==============================================================");
    }
}
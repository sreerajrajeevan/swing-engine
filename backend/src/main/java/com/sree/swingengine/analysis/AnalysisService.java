package com.sree.swingengine.analysis;

import com.sree.swingengine.analysis.common.AnalysisContext;
import com.sree.swingengine.analysis.momentum.MomentumAnalyzer;
import com.sree.swingengine.analysis.model.StockAnalysisResult;
import com.sree.swingengine.analysis.overall.OverallScoreCalculator;
import com.sree.swingengine.analysis.overall.OverallScoreResult;
import com.sree.swingengine.analysis.trend.TrendAnalysisResult;
import com.sree.swingengine.analysis.trend.TrendAnalyzer;
import com.sree.swingengine.analysis.volume.VolumeAnalyzer;
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
    private final MomentumAnalyzer momentumAnalyzer;
    private final VolumeAnalyzer volumeAnalyzer;
    private final OverallScoreCalculator overallScoreCalculator;

    public void analyzeStocks() {

        List<Stock> stocks = stockRepository.findAll();

        List<StockAnalysisResult> shortlisted = new ArrayList<>();

        int success = 0;
        int failed = 0;

        long start = System.currentTimeMillis();

        for (Stock stock : stocks) {

            try {

                StockAnalysisResult analysis = analyze(stock);
                TrendAnalysisResult trend = analysis.getTrend();

                success++;

                if (trend.getScore() >= MINIMUM_SCORE) {

                    shortlisted.add(
                            StockAnalysisResult.builder()
                                    .stock(stock)
                                    .trend(trend)
                                    .momentum(analysis.getMomentum())
                                    .volume(analysis.getVolume())
                                    .overall(analysis.getOverall())
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

    private StockAnalysisResult analyze(Stock stock) {

        AnalysisContext context = contextFactory.build(stock);

        TrendAnalysisResult trend = trendAnalyzer.analyze(context);
        var momentum = momentumAnalyzer.analyze(context);
        var volume = volumeAnalyzer.analyze(context);
        OverallScoreResult overall = overallScoreCalculator.calculate(trend, momentum, volume);

        return StockAnalysisResult.builder()
                .stock(stock)
                .trend(trend)
                .momentum(momentum)
                .volume(volume)
                .overall(overall)
                .build();
    }

    private void printSummary(List<StockAnalysisResult> shortlisted,
                              int totalStocks,
                              int success,
                              int failed,
                              long startTime) {

        long timeTaken = System.currentTimeMillis() - startTime;

        log.info("");
        log.info("==============================================================");
        log.info("       SWING ENGINE - TREND, MOMENTUM & VOLUME ANALYSIS");
        log.info("==============================================================");
        log.info("");

        if (shortlisted.isEmpty()) {

            log.info("No qualifying stocks found.");

        } else {

            log.info(String.format("%-5s %-18s %-7s %-14s %-10s %-14s %-8s %-14s %-14s",
                    "Rank",
                    "Symbol",
                    "Trend",
                    "Trend Strength",
                    "Momentum",
                    "Momentum Strength", "Volume", "Volume Strength", "Overall (pre-risk)"));

            log.info("--------------------------------------------------------------");

            int rank = 1;

            for (StockAnalysisResult result : shortlisted) {

                log.info(String.format("%-5d %-18s %-7d %-14s %-10d %-14s %-8d %-14s %-14.2f",
                        rank++,
                        result.getStock().getSymbol(),
                        result.getTrend().getScore(),
                        result.getTrend().getStrength(),
                        result.getMomentum().getScore(),
                        result.getMomentum().getStrength(), result.getVolume().getScore(), result.getVolume().getStrength(),
                        result.getOverall().getProvisionalScore()));

                result.getTrend().getScoreBreakdown()
                        .forEach((key, value) ->
                                log.info("      Trend - {} : {}", key, value));

                result.getMomentum().getScoreBreakdown()
                        .forEach((key, value) ->
                                log.info("      Momentum - {} : {}", key, value));

                result.getVolume().getScoreBreakdown()
                        .forEach((key, value) -> log.info("      Volume - {} : {}", key, value));

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

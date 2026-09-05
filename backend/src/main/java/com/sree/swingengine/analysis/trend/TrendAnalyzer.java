package com.sree.swingengine.analysis.trend;

import com.sree.swingengine.analysis.common.AnalysisContext;
import com.sree.swingengine.analysis.common.Analyzer;
import com.sree.swingengine.entity.DailyIndicator;
import com.sree.swingengine.market.enums.TrendDirection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TrendAnalyzer implements Analyzer<TrendAnalysisResult> {

    private static final int EMA_ALIGNMENT_SCORE = 30;
    private static final int SUPERTREND_SCORE = 25;
    private static final int PRICE_ABOVE_EMA20_SCORE = 25;

    private static final int ADX_STRONG_SCORE = 20;
    private static final int ADX_MEDIUM_SCORE = 15;
    private static final int ADX_WEAK_SCORE = 10;

    @Override
    public TrendAnalysisResult analyze(AnalysisContext context) {

        DailyIndicator indicator = context.getCurrentIndicator();

        int score = 0;

        List<String> reasons = new ArrayList<>();

        Map<String, Integer> scoreBreakdown = new LinkedHashMap<>();

        /*
         * EMA Alignment
         */
        if (indicator.getEma20() != null
                && indicator.getEma50() != null
                && indicator.getEma200() != null
                && indicator.getEma20().compareTo(indicator.getEma50()) > 0
                && indicator.getEma50().compareTo(indicator.getEma200()) > 0) {

            score += EMA_ALIGNMENT_SCORE;
            scoreBreakdown.put("EMA Alignment", EMA_ALIGNMENT_SCORE);
            reasons.add("EMA20 > EMA50 > EMA200");
        }

        /*
         * Supertrend
         */
        if (indicator.getSupertrendDirection() == TrendDirection.BUY) {

            score += SUPERTREND_SCORE;
            scoreBreakdown.put("Supertrend", SUPERTREND_SCORE);
            reasons.add("Supertrend BUY");
        }

        /*
         * ADX Strength
         */
        if (indicator.getAdx() != null) {

            double adx = indicator.getAdx().doubleValue();

            if (adx >= 30) {

                score += ADX_STRONG_SCORE;
                scoreBreakdown.put("ADX", ADX_STRONG_SCORE);
                reasons.add("ADX > 30 (Very Strong Trend)");

            } else if (adx >= 25) {

                score += ADX_MEDIUM_SCORE;
                scoreBreakdown.put("ADX", ADX_MEDIUM_SCORE);
                reasons.add("ADX > 25 (Strong Trend)");

            } else if (adx >= 20) {

                score += ADX_WEAK_SCORE;
                scoreBreakdown.put("ADX", ADX_WEAK_SCORE);
                reasons.add("ADX > 20 (Developing Trend)");
            }
        }

        /*
         * Price Above EMA20
         */
        if (indicator.getEma20() != null
                && context.getCurrentPrice() != null
                && context.getCurrentPrice().getClose() != null
                && context.getCurrentPrice().getClose().compareTo(indicator.getEma20()) > 0) {

            score += PRICE_ABOVE_EMA20_SCORE;
            scoreBreakdown.put("Price Above EMA20", PRICE_ABOVE_EMA20_SCORE);
            reasons.add("Closing Price Above EMA20");
        }

        TrendStrength strength;

        if (score >= 85) {
            strength = TrendStrength.VERY_STRONG;
        } else if (score >= 70) {
            strength = TrendStrength.STRONG;
        } else if (score >= 50) {
            strength = TrendStrength.NEUTRAL;
        } else if (score >= 25) {
            strength = TrendStrength.WEAK;
        } else {
            strength = TrendStrength.VERY_WEAK;
        }

        return TrendAnalysisResult.builder()
                .bullish(score >= 70)
                .score(score)
                .strength(strength)
                .reasons(reasons)
                .scoreBreakdown(scoreBreakdown)
                .build();
    }
}
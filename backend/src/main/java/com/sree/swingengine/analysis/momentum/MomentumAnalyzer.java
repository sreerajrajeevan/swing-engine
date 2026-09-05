package com.sree.swingengine.analysis.momentum;

import com.sree.swingengine.analysis.common.AnalysisContext;
import com.sree.swingengine.analysis.common.Analyzer;
import com.sree.swingengine.entity.DailyIndicator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class MomentumAnalyzer implements Analyzer<MomentumAnalysisResult> {

    /*
     * 100-point model:
     * RSI14: 30 points, MACD: 30 points, Stochastic RSI: 20 points, MFI: 20 points.
     * The MACD histogram acceleration bonus uses the previous persisted indicator when present.
     */
    private static final int RSI_IDEAL_SCORE = 30;
    private static final int RSI_POSITIVE_SCORE = 25;
    private static final int RSI_EXTENDED_SCORE = 20;
    private static final int RSI_EARLY_SCORE = 15;

    private static final int MACD_ABOVE_SIGNAL_SCORE = 15;
    private static final int MACD_POSITIVE_HISTOGRAM_SCORE = 10;
    private static final int MACD_ACCELERATING_SCORE = 5;

    private static final int STOCHASTIC_IDEAL_SCORE = 20;
    private static final int STOCHASTIC_RECOVERING_SCORE = 15;
    private static final int STOCHASTIC_EXTENDED_SCORE = 10;

    private static final int MFI_IDEAL_SCORE = 20;
    private static final int MFI_POSITIVE_SCORE = 12;
    private static final int MFI_EXTENDED_SCORE = 8;

    @Override
    public MomentumAnalysisResult analyze(AnalysisContext context) {
        DailyIndicator indicator = context.getCurrentIndicator();
        if (indicator == null) {
            throw new IllegalArgumentException("Analysis context must contain a current indicator");
        }

        int score = 0;
        List<String> reasons = new ArrayList<>();
        Map<String, Integer> scoreBreakdown = new LinkedHashMap<>();

        score += scoreRsi(indicator, reasons, scoreBreakdown);
        score += scoreMacd(indicator, findPreviousIndicator(context, indicator), reasons, scoreBreakdown);
        score += scoreStochasticRsi(indicator, reasons, scoreBreakdown);
        score += scoreMfi(indicator, reasons, scoreBreakdown);

        return MomentumAnalysisResult.builder()
                .bullish(score >= 70)
                .score(score)
                .strength(toStrength(score))
                .reasons(reasons)
                .scoreBreakdown(scoreBreakdown)
                .build();
    }

    private int scoreRsi(DailyIndicator indicator, List<String> reasons, Map<String, Integer> breakdown) {
        BigDecimal rsi = indicator.getRsi14();
        if (rsi == null) {
            return 0;
        }
        if (inRange(rsi, 55, 70)) {
            return add("RSI", RSI_IDEAL_SCORE, "RSI14 is in the 55-70 momentum zone", reasons, breakdown);
        }
        if (inRange(rsi, 50, 55)) {
            return add("RSI", RSI_POSITIVE_SCORE, "RSI14 is positive", reasons, breakdown);
        }
        if (inRange(rsi, 70, 75)) {
            return add("RSI", RSI_EXTENDED_SCORE, "RSI14 is strong but becoming extended", reasons, breakdown);
        }
        if (inRange(rsi, 45, 50)) {
            return add("RSI", RSI_EARLY_SCORE, "RSI14 is improving but not yet positive", reasons, breakdown);
        }
        return 0;
    }

    private int scoreMacd(DailyIndicator indicator, DailyIndicator previous, List<String> reasons, Map<String, Integer> breakdown) {
        int score = 0;
        if (indicator.getMacd() != null && indicator.getMacdSignal() != null
                && indicator.getMacd().compareTo(indicator.getMacdSignal()) > 0) {
            score += add("MACD above signal", MACD_ABOVE_SIGNAL_SCORE, "MACD is above its signal line", reasons, breakdown);
        }
        if (indicator.getMacdHistogram() != null && indicator.getMacdHistogram().signum() > 0) {
            score += add("MACD positive histogram", MACD_POSITIVE_HISTOGRAM_SCORE, "MACD histogram is positive", reasons, breakdown);
        }
        if (indicator.getMacdHistogram() != null && previous != null && previous.getMacdHistogram() != null
                && indicator.getMacdHistogram().compareTo(previous.getMacdHistogram()) > 0) {
            score += add("MACD accelerating", MACD_ACCELERATING_SCORE, "MACD histogram is increasing from the prior session", reasons, breakdown);
        }
        return score;
    }

    private int scoreStochasticRsi(DailyIndicator indicator, List<String> reasons, Map<String, Integer> breakdown) {
        if (indicator.getStochasticK() == null || indicator.getStochasticD() == null
                || indicator.getStochasticK().compareTo(indicator.getStochasticD()) <= 0) {
            return 0;
        }
        BigDecimal k = indicator.getStochasticK();
        if (inRange(k, 50, 80)) {
            return add("Stochastic RSI", STOCHASTIC_IDEAL_SCORE, "Stochastic RSI is bullish in the 50-80 zone", reasons, breakdown);
        }
        if (inRange(k, 20, 50)) {
            return add("Stochastic RSI", STOCHASTIC_RECOVERING_SCORE, "Stochastic RSI is bullish and recovering", reasons, breakdown);
        }
        if (inRange(k, 80, 90)) {
            return add("Stochastic RSI", STOCHASTIC_EXTENDED_SCORE, "Stochastic RSI is bullish but extended", reasons, breakdown);
        }
        return 0;
    }

    private int scoreMfi(DailyIndicator indicator, List<String> reasons, Map<String, Integer> breakdown) {
        BigDecimal mfi = indicator.getMfi();
        if (mfi == null) {
            return 0;
        }
        if (inRange(mfi, 50, 80)) {
            return add("MFI", MFI_IDEAL_SCORE, "Money flow is positive", reasons, breakdown);
        }
        if (inRange(mfi, 40, 50)) {
            return add("MFI", MFI_POSITIVE_SCORE, "Money flow is improving", reasons, breakdown);
        }
        if (inRange(mfi, 80, 90)) {
            return add("MFI", MFI_EXTENDED_SCORE, "Money flow is positive but extended", reasons, breakdown);
        }
        return 0;
    }

    private DailyIndicator findPreviousIndicator(AnalysisContext context, DailyIndicator current) {
        if (context.getIndicatorHistory() == null || current.getTradingDate() == null) {
            return null;
        }
        return context.getIndicatorHistory().stream()
                .filter(candidate -> candidate != null && candidate.getTradingDate() != null)
                .filter(candidate -> candidate.getTradingDate().isBefore(current.getTradingDate()))
                .max(Comparator.comparing(DailyIndicator::getTradingDate))
                .orElse(null);
    }

    private boolean inRange(BigDecimal value, int lowerInclusive, int upperExclusive) {
        return value.compareTo(BigDecimal.valueOf(lowerInclusive)) >= 0
                && value.compareTo(BigDecimal.valueOf(upperExclusive)) < 0;
    }

    private int add(String key, int points, String reason, List<String> reasons, Map<String, Integer> breakdown) {
        breakdown.put(key, points);
        reasons.add(reason);
        return points;
    }

    private MomentumStrength toStrength(int score) {
        if (score >= 85) return MomentumStrength.VERY_STRONG;
        if (score >= 70) return MomentumStrength.STRONG;
        if (score >= 50) return MomentumStrength.NEUTRAL;
        if (score >= 25) return MomentumStrength.WEAK;
        return MomentumStrength.VERY_WEAK;
    }
}

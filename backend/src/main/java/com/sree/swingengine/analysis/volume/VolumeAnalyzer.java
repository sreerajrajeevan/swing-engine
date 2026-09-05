package com.sree.swingengine.analysis.volume;

import com.sree.swingengine.analysis.common.AnalysisContext;
import com.sree.swingengine.analysis.common.Analyzer;
import com.sree.swingengine.entity.DailyIndicator;
import com.sree.swingengine.entity.DailyPrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class VolumeAnalyzer implements Analyzer<VolumeAnalysisResult> {

    /* 100 points: relative volume 40, OBV direction 25, price-volume confirmation 20, MFI 15. */
    private static final int RELATIVE_VOLUME_EXCEPTIONAL_SCORE = 40;
    private static final int RELATIVE_VOLUME_STRONG_SCORE = 30;
    private static final int RELATIVE_VOLUME_POSITIVE_SCORE = 20;
    private static final int RELATIVE_VOLUME_NORMAL_SCORE = 10;
    private static final int OBV_RISING_SCORE = 25;
    private static final int PRICE_VOLUME_CONFIRMATION_SCORE = 20;
    private static final int MFI_POSITIVE_SCORE = 15;
    private static final int MFI_IMPROVING_SCORE = 8;
    private static final int MFI_EXTENDED_SCORE = 6;

    @Override
    public VolumeAnalysisResult analyze(AnalysisContext context) {
        DailyIndicator indicator = context.getCurrentIndicator();
        if (indicator == null) throw new IllegalArgumentException("Analysis context must contain a current indicator");

        DailyIndicator previousIndicator = findPreviousIndicator(context, indicator);
        DailyPrice currentPrice = context.getCurrentPrice();
        DailyPrice previousPrice = findPreviousPrice(context, currentPrice);
        List<String> reasons = new ArrayList<>();
        Map<String, Integer> breakdown = new LinkedHashMap<>();

        int score = scoreRelativeVolume(indicator, currentPrice, reasons, breakdown)
                + scoreObv(indicator, previousIndicator, reasons, breakdown)
                + scorePriceVolumeConfirmation(indicator, currentPrice, previousPrice, reasons, breakdown)
                + scoreMfi(indicator, reasons, breakdown);

        return VolumeAnalysisResult.builder().bullish(score >= 70).score(score)
                .strength(toStrength(score)).reasons(reasons).scoreBreakdown(breakdown).build();
    }

    private int scoreRelativeVolume(DailyIndicator indicator, DailyPrice currentPrice,
                                    List<String> reasons, Map<String, Integer> breakdown) {
        BigDecimal relativeVolume = indicator.getRelativeVolume();
        if (relativeVolume == null && currentPrice != null && currentPrice.getVolume() != null
                && indicator.getVolumeSma20() != null && indicator.getVolumeSma20().signum() > 0) {
            relativeVolume = BigDecimal.valueOf(currentPrice.getVolume()).divide(indicator.getVolumeSma20(), 4, RoundingMode.HALF_UP);
        }
        if (relativeVolume == null) return 0;
        if (relativeVolume.compareTo(BigDecimal.valueOf(2)) >= 0) return add("Relative volume", RELATIVE_VOLUME_EXCEPTIONAL_SCORE, "Volume is at least 2x its 20-day average", reasons, breakdown);
        if (relativeVolume.compareTo(BigDecimal.valueOf(1.5)) >= 0) return add("Relative volume", RELATIVE_VOLUME_STRONG_SCORE, "Volume is at least 1.5x its 20-day average", reasons, breakdown);
        if (relativeVolume.compareTo(BigDecimal.valueOf(1.2)) >= 0) return add("Relative volume", RELATIVE_VOLUME_POSITIVE_SCORE, "Volume is above its 20-day average", reasons, breakdown);
        if (relativeVolume.compareTo(BigDecimal.ONE) >= 0) return add("Relative volume", RELATIVE_VOLUME_NORMAL_SCORE, "Volume is meeting its 20-day average", reasons, breakdown);
        return 0;
    }

    private int scoreObv(DailyIndicator indicator, DailyIndicator previous,
                         List<String> reasons, Map<String, Integer> breakdown) {
        if (indicator.getObv() != null && previous != null && previous.getObv() != null && indicator.getObv().compareTo(previous.getObv()) > 0) {
            return add("OBV", OBV_RISING_SCORE, "On-balance volume is increasing from the prior session", reasons, breakdown);
        }
        return 0;
    }

    private int scorePriceVolumeConfirmation(DailyIndicator indicator, DailyPrice current, DailyPrice previous,
                                             List<String> reasons, Map<String, Integer> breakdown) {
        if (current == null || previous == null || current.getClose() == null || previous.getClose() == null || current.getVolume() == null || indicator.getVolumeSma20() == null) return 0;
        if (current.getClose().compareTo(previous.getClose()) > 0 && BigDecimal.valueOf(current.getVolume()).compareTo(indicator.getVolumeSma20()) > 0) {
            return add("Price-volume confirmation", PRICE_VOLUME_CONFIRMATION_SCORE, "Price rose on above-average volume", reasons, breakdown);
        }
        return 0;
    }

    private int scoreMfi(DailyIndicator indicator, List<String> reasons, Map<String, Integer> breakdown) {
        BigDecimal mfi = indicator.getMfi();
        if (mfi == null) return 0;
        if (inRange(mfi, 50, 80)) return add("MFI", MFI_POSITIVE_SCORE, "Money flow confirms positive volume", reasons, breakdown);
        if (inRange(mfi, 40, 50)) return add("MFI", MFI_IMPROVING_SCORE, "Money flow is improving", reasons, breakdown);
        if (inRange(mfi, 80, 90)) return add("MFI", MFI_EXTENDED_SCORE, "Money flow is positive but extended", reasons, breakdown);
        return 0;
    }

    private DailyIndicator findPreviousIndicator(AnalysisContext context, DailyIndicator current) {
        if (context.getIndicatorHistory() == null || current.getTradingDate() == null) return null;
        return context.getIndicatorHistory().stream().filter(candidate -> candidate != null && candidate.getTradingDate() != null)
                .filter(candidate -> candidate.getTradingDate().isBefore(current.getTradingDate()))
                .max(Comparator.comparing(DailyIndicator::getTradingDate)).orElse(null);
    }

    private DailyPrice findPreviousPrice(AnalysisContext context, DailyPrice current) {
        if (context.getPriceHistory() == null || current == null || current.getTradingDate() == null) return null;
        return context.getPriceHistory().stream().filter(candidate -> candidate != null && candidate.getTradingDate() != null)
                .filter(candidate -> candidate.getTradingDate().isBefore(current.getTradingDate()))
                .max(Comparator.comparing(DailyPrice::getTradingDate)).orElse(null);
    }

    private boolean inRange(BigDecimal value, int lowerInclusive, int upperExclusive) {
        return value.compareTo(BigDecimal.valueOf(lowerInclusive)) >= 0 && value.compareTo(BigDecimal.valueOf(upperExclusive)) < 0;
    }

    private int add(String key, int points, String reason, List<String> reasons, Map<String, Integer> breakdown) {
        breakdown.put(key, points); reasons.add(reason); return points;
    }

    private VolumeStrength toStrength(int score) {
        if (score >= 85) return VolumeStrength.VERY_STRONG;
        if (score >= 70) return VolumeStrength.STRONG;
        if (score >= 50) return VolumeStrength.NEUTRAL;
        if (score >= 25) return VolumeStrength.WEAK;
        return VolumeStrength.VERY_WEAK;
    }
}

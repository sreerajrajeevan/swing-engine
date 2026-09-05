package com.sree.swingengine.analysis.overall;

import com.sree.swingengine.analysis.momentum.MomentumAnalysisResult;
import com.sree.swingengine.analysis.trend.TrendAnalysisResult;
import com.sree.swingengine.analysis.volume.VolumeAnalysisResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OverallScoreCalculator {

    public static final double TREND_WEIGHT = 0.35;
    public static final double MOMENTUM_WEIGHT = 0.25;
    public static final double VOLUME_WEIGHT = 0.25;
    public static final double RISK_WEIGHT = 0.15;
    private static final double PRE_RISK_MAXIMUM = TREND_WEIGHT + MOMENTUM_WEIGHT + VOLUME_WEIGHT;

    public OverallScoreResult calculate(TrendAnalysisResult trend,
                                        MomentumAnalysisResult momentum,
                                        VolumeAnalysisResult volume) {
        return calculate(trend, momentum, volume, null);
    }

    /**
     * The risk score is intentionally an input rather than a fallback calculation.
     * A future Risk Analyzer can provide its normalized 0-100 suitability score here.
     */
    public OverallScoreResult calculate(TrendAnalysisResult trend,
                                        MomentumAnalysisResult momentum,
                                        VolumeAnalysisResult volume,
                                        Integer riskScore) {
        Integer trendValue = scoreOf(trend == null ? null : trend.getScore());
        Integer momentumValue = scoreOf(momentum == null ? null : momentum.getScore());
        Integer volumeValue = scoreOf(volume == null ? null : volume.getScore());
        Integer riskValue = scoreOf(riskScore);

        Map<String, Double> weightedBreakdown = new LinkedHashMap<>();
        List<String> reasons = new ArrayList<>();
        double provisional = addComponent("Trend", trendValue, TREND_WEIGHT, weightedBreakdown, reasons);
        provisional += addComponent("Momentum", momentumValue, MOMENTUM_WEIGHT, weightedBreakdown, reasons);
        provisional += addComponent("Volume", volumeValue, VOLUME_WEIGHT, weightedBreakdown, reasons);

        double normalized = round(provisional / PRE_RISK_MAXIMUM);
        Double finalScore = null;
        double classificationScore = normalized;
        if (riskValue == null) {
            reasons.add("Risk score unavailable; provisional score excludes the reserved 15% risk weight");
        } else {
            double riskContribution = addComponent("Risk", riskValue, RISK_WEIGHT, weightedBreakdown, reasons);
            finalScore = round(provisional + riskContribution);
            classificationScore = finalScore;
        }

        return OverallScoreResult.builder()
                .trendScore(trendValue)
                .momentumScore(momentumValue)
                .volumeScore(volumeValue)
                .riskScore(riskValue)
                .weightedBreakdown(weightedBreakdown)
                .provisionalScore(round(provisional))
                .provisionalScoreNormalized(normalized)
                .finalScore(finalScore)
                .strength(toStrength(classificationScore))
                .reasons(reasons)
                .build();
    }

    private Integer scoreOf(Integer score) {
        if (score == null) {
            return null;
        }
        return Math.max(0, Math.min(100, score));
    }

    private double addComponent(String name, Integer score, double weight,
                                Map<String, Double> breakdown, List<String> reasons) {
        if (score == null) {
            reasons.add(name + " analysis unavailable");
            return 0;
        }
        double contribution = round(score * weight);
        breakdown.put(name, contribution);
        reasons.add(name + " contributed " + contribution + " points");
        return contribution;
    }

    private OverallScoreStrength toStrength(double score) {
        if (score >= 85) return OverallScoreStrength.VERY_STRONG;
        if (score >= 70) return OverallScoreStrength.STRONG;
        if (score >= 50) return OverallScoreStrength.NEUTRAL;
        if (score >= 25) return OverallScoreStrength.WEAK;
        return OverallScoreStrength.VERY_WEAK;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

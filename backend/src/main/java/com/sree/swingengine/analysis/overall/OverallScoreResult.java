package com.sree.swingengine.analysis.overall;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Composite V1 score. Before Risk Analyzer exists, provisionalScore is deliberately
 * limited to the 85 points available from Trend, Momentum, and Volume. Once riskScore
 * is supplied, finalScore is the complete score on the intended 100-point scale.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverallScoreResult {

    private Integer trendScore;
    private Integer momentumScore;
    private Integer volumeScore;
    private Integer riskScore;

    @Builder.Default
    private Map<String, Double> weightedBreakdown = new LinkedHashMap<>();

    /** Score from currently available non-risk components, with a maximum of 85. */
    private double provisionalScore;

    /** Provisional score expressed on a 100-point scale for comparison only. */
    private double provisionalScoreNormalized;

    /** Complete 100-point score. Null until a Risk Analyzer provides a score. */
    private Double finalScore;

    private OverallScoreStrength strength;

    @Builder.Default
    private List<String> reasons = List.of();
}

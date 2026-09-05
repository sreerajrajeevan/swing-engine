package com.sree.swingengine.analysis.trend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendAnalysisResult {

    private boolean bullish;

    private int score;

    private TrendStrength strength;

    private List<String> reasons;

    @Builder.Default
    private Map<String, Integer> scoreBreakdown = new LinkedHashMap<>();

}
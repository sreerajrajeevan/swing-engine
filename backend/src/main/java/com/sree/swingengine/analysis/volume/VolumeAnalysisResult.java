package com.sree.swingengine.analysis.volume;

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
public class VolumeAnalysisResult {
    private boolean bullish;
    private int score;
    private VolumeStrength strength;
    private List<String> reasons;

    @Builder.Default
    private Map<String, Integer> scoreBreakdown = new LinkedHashMap<>();
}

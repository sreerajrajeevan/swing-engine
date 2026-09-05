package com.sree.swingengine.analysis.model;

import com.sree.swingengine.analysis.momentum.MomentumAnalysisResult;
import com.sree.swingengine.analysis.trend.TrendAnalysisResult;
import com.sree.swingengine.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAnalysisResult {

    private Stock stock;

    private TrendAnalysisResult trend;

    private MomentumAnalysisResult momentum;

}

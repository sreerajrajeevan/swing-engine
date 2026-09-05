package com.sree.swingengine.analysis.common;

import com.sree.swingengine.entity.DailyIndicator;
import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisContext {

    private Stock stock;
    private DailyPrice currentPrice;
    private DailyIndicator currentIndicator;
    private List<DailyPrice> priceHistory;
    private List<DailyIndicator> indicatorHistory;
}
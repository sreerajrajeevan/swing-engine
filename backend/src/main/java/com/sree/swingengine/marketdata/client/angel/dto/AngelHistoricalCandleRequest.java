package com.sree.swingengine.marketdata.client.angel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AngelHistoricalCandleRequest {

    private String exchange;
    private String symboltoken;
    private String interval;
    private String fromdate;
    private String todate;
}

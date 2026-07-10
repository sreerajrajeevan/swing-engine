package com.sree.swingengine.market.client.angel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AngelHistoricalCandleResponse {

    private boolean status;
    private String message;
    private List<List<Object>> data;

}
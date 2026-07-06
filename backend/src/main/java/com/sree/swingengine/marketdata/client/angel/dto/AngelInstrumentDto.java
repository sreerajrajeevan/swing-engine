package com.sree.swingengine.marketdata.client.angel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AngelInstrumentDto {

    private String token;
    private String symbol;
    private String name;
    private String exch_seg;
    private String instrumenttype;

}

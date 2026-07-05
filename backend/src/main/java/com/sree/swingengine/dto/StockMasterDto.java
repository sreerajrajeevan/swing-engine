package com.sree.swingengine.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockMasterDto {

    private String symbol;
    private String companyName;
    private String industry;
    private String isinCode;

}
package com.sree.swingengine.market.indicator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MacdResult {

    private final List<BigDecimal> macd;
    private final List<BigDecimal> signal;
    private final List<BigDecimal> histogram;
}
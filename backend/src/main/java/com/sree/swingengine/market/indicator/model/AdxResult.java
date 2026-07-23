package com.sree.swingengine.market.indicator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AdxResult {

    private final List<BigDecimal> adx;
    private final List<BigDecimal> plusDi;
    private final List<BigDecimal> minusDi;
}
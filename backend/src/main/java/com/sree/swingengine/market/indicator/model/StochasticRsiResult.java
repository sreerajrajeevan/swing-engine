package com.sree.swingengine.market.indicator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StochasticRsiResult {

    private List<BigDecimal> k;

    private List<BigDecimal> d;
}
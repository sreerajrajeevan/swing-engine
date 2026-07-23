package com.sree.swingengine.market.indicator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class DirectionalMovement {

    private final List<BigDecimal> plusDm;
    private final List<BigDecimal> minusDm;
}
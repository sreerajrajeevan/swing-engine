package com.sree.swingengine.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "daily_indicator",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"stock_id", "trading_date"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyIndicator extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "trading_date", nullable = false)
    private LocalDate tradingDate;

    @Column(precision = 19, scale = 4)
    private BigDecimal ema20;

    @Column(precision = 19, scale = 4)
    private BigDecimal ema50;

    @Column(precision = 19, scale = 4)
    private BigDecimal ema200;

    @Column(precision = 10, scale = 4)
    private BigDecimal rsi14;

    @Column(precision = 19, scale = 4)
    private BigDecimal macd;

    @Column(precision = 19, scale = 4)
    private BigDecimal macdSignal;

    @Column(precision = 19, scale = 4)
    private BigDecimal macdHistogram;

    @Column(precision = 10, scale = 4)
    private BigDecimal adx;

    @Column(precision = 10, scale = 4)
    private BigDecimal plusDi;

    @Column(precision = 10, scale = 4)
    private BigDecimal minusDi;

    @Column(precision = 19, scale = 10)
    private BigDecimal atr14;

    @Column(name = "bb_upper", precision = 19, scale = 10)
    private BigDecimal bbUpper;

    @Column(name = "bb_middle", precision = 19, scale = 10)
    private BigDecimal bbMiddle;

    @Column(name = "bb_lower", precision = 19, scale = 10)
    private BigDecimal bbLower;

    @Column(precision = 19, scale = 10)
    private BigDecimal stochasticK;

    @Column(precision = 19, scale = 10)
    private BigDecimal stochasticD;
}
package com.sree.swingengine.analysis.momentum;

import com.sree.swingengine.analysis.common.AnalysisContext;
import com.sree.swingengine.entity.DailyIndicator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MomentumAnalyzerTest {

    private final MomentumAnalyzer analyzer = new MomentumAnalyzer();

    @Test
    void awardsAllOneHundredPointsForAlignedMomentum() {
        DailyIndicator previous = indicator(LocalDate.of(2026, 9, 3), "0.40");
        DailyIndicator current = indicator(LocalDate.of(2026, 9, 4), "0.60");

        MomentumAnalysisResult result = analyzer.analyze(AnalysisContext.builder()
                .currentIndicator(current)
                .indicatorHistory(List.of(previous, current))
                .build());

        assertThat(result.getScore()).isEqualTo(100);
        assertThat(result.isBullish()).isTrue();
        assertThat(result.getStrength()).isEqualTo(MomentumStrength.VERY_STRONG);
        assertThat(result.getScoreBreakdown()).containsEntry("MACD accelerating", 5);
    }

    @Test
    void doesNotAwardAccelerationWithoutPriorIndicator() {
        DailyIndicator current = indicator(LocalDate.of(2026, 9, 4), "0.60");

        MomentumAnalysisResult result = analyzer.analyze(AnalysisContext.builder()
                .currentIndicator(current)
                .indicatorHistory(List.of(current))
                .build());

        assertThat(result.getScore()).isEqualTo(95);
        assertThat(result.getScoreBreakdown()).doesNotContainKey("MACD accelerating");
    }

    @Test
    void scoresRsiAtTheStartOfTheIdealRange() {
        DailyIndicator current = indicator(LocalDate.of(2026, 9, 4), "0.60");
        current.setRsi14(BigDecimal.valueOf(55));

        MomentumAnalysisResult result = analyzer.analyze(AnalysisContext.builder()
                .currentIndicator(current)
                .build());

        assertThat(result.getScoreBreakdown()).containsEntry("RSI", 30);
    }

    private DailyIndicator indicator(LocalDate date, String histogram) {
        return DailyIndicator.builder()
                .tradingDate(date)
                .rsi14(BigDecimal.valueOf(60))
                .macd(BigDecimal.valueOf(2))
                .macdSignal(BigDecimal.ONE)
                .macdHistogram(new BigDecimal(histogram))
                .stochasticK(BigDecimal.valueOf(65))
                .stochasticD(BigDecimal.valueOf(55))
                .mfi(BigDecimal.valueOf(60))
                .build();
    }
}

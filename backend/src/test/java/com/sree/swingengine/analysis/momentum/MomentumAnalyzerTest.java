package com.sree.swingengine.analysis.momentum;

import com.sree.swingengine.analysis.common.AnalysisContext;
import com.sree.swingengine.entity.DailyIndicator;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MomentumAnalyzerTest {
    private final MomentumAnalyzer analyzer = new MomentumAnalyzer();

    @Test void awardsAllPointsForAlignedBullishMomentum() {
        DailyIndicator previous = bullish(LocalDate.of(2026, 9, 3), ".40");
        DailyIndicator current = bullish(LocalDate.of(2026, 9, 4), ".60");
        MomentumAnalysisResult result = analyze(current, List.of(previous, current));
        assertThat(result.getScore()).isEqualTo(100);
        assertThat(result.isBullish()).isTrue();
        assertThat(result.getStrength()).isEqualTo(MomentumStrength.VERY_STRONG);
        assertThat(result.getScoreBreakdown()).containsEntry("MACD accelerating", 5);
    }

    @Test void doesNotAwardMacdAccelerationWithoutEarlierOrWhenFallingLatestHistory() {
        DailyIndicator current = bullish(LocalDate.of(2026, 9, 4), ".60");
        assertThat(analyze(current, List.of(current)).getScore()).isEqualTo(95);

        DailyIndicator old = bullish(LocalDate.of(2026, 9, 1), ".20");
        DailyIndicator latestEarlier = bullish(LocalDate.of(2026, 9, 3), ".80");
        MomentumAnalysisResult result = analyze(current, List.of(old, current, latestEarlier));
        assertThat(result.getScore()).isEqualTo(95);
        assertThat(result.getScoreBreakdown()).doesNotContainKey("MACD accelerating");
    }

    @Test void appliesRsiBoundariesWithoutDoubleCounting() {
        assertThat(rsi("44.99")).isZero();
        assertThat(rsi("45")).isEqualTo(15);
        assertThat(rsi("50")).isEqualTo(25);
        assertThat(rsi("55")).isEqualTo(30);
        assertThat(rsi("70")).isEqualTo(20);
        assertThat(rsi("75")).isZero();
    }

    @Test void requiresBullishStochasticCrossAndHonorsBoundaries() {
        assertThat(stochastic("20", "19")).isEqualTo(15);
        assertThat(stochastic("50", "49")).isEqualTo(20);
        assertThat(stochastic("80", "79")).isEqualTo(10);
        assertThat(stochastic("90", "89")).isZero();
        assertThat(stochastic("65", "65")).isZero();
        assertThat(stochastic("65", "66")).isZero();
    }

    @Test void appliesMfiBoundariesAndIgnoresOverextendedValue() {
        assertThat(mfi("39.99")).isZero();
        assertThat(mfi("40")).isEqualTo(12);
        assertThat(mfi("50")).isEqualTo(20);
        assertThat(mfi("80")).isEqualTo(8);
        assertThat(mfi("90")).isZero();
    }

    @Test void bearishAndMissingValuesAreSafeAndScoreZero() {
        DailyIndicator bearish = DailyIndicator.builder().tradingDate(LocalDate.of(2026, 9, 4))
                .rsi14(BigDecimal.valueOf(30)).macd(BigDecimal.ONE).macdSignal(BigDecimal.TEN)
                .macdHistogram(BigDecimal.valueOf(-1)).stochasticK(BigDecimal.TEN).stochasticD(BigDecimal.valueOf(20))
                .mfi(BigDecimal.valueOf(20)).build();
        MomentumAnalysisResult bearishResult = analyze(bearish, List.of(bearish));
        MomentumAnalysisResult missingResult = analyze(DailyIndicator.builder().tradingDate(LocalDate.of(2026, 9, 4)).build(), null);
        assertThat(bearishResult.getScore()).isZero();
        assertThat(bearishResult.isBullish()).isFalse();
        assertThat(missingResult.getScore()).isZero();
        assertThat(missingResult.getReasons()).isEmpty();
    }

    @Test void rejectsMissingCurrentIndicator() {
        assertThatThrownBy(() -> analyzer.analyze(AnalysisContext.builder().build()))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Analysis context must contain a current indicator");
    }

    @Test void ignoresNullAndSameDayHistoryAndUsesLatestStrictlyEarlierIndicator() {
        DailyIndicator current = bullish(LocalDate.of(2026, 9, 4), ".60");
        DailyIndicator sameDay = bullish(LocalDate.of(2026, 9, 4), ".90");
        DailyIndicator earlier = bullish(LocalDate.of(2026, 9, 3), ".40");
        MomentumAnalysisResult result = analyze(current, java.util.Arrays.asList(null, sameDay, earlier, current));
        assertThat(result.getScoreBreakdown()).containsEntry("MACD accelerating", 5);
    }

    @Test void doesNotAccidentallyAwardMacdAccelerationWhenCurrentOrHistoryDateIsMissing() {
        DailyIndicator current = bullish(null, ".60");
        DailyIndicator earlier = bullish(LocalDate.of(2026, 9, 3), ".40");
        assertThat(analyze(current, List.of(earlier, current)).getScore()).isEqualTo(95);
    }

    private int rsi(String value) { return analyze(DailyIndicator.builder().tradingDate(LocalDate.now()).rsi14(new BigDecimal(value)).build(), null).getScore(); }
    private int stochastic(String k, String d) { return analyze(DailyIndicator.builder().tradingDate(LocalDate.now()).stochasticK(new BigDecimal(k)).stochasticD(new BigDecimal(d)).build(), null).getScore(); }
    private int mfi(String value) { return analyze(DailyIndicator.builder().tradingDate(LocalDate.now()).mfi(new BigDecimal(value)).build(), null).getScore(); }
    private MomentumAnalysisResult analyze(DailyIndicator current, List<DailyIndicator> history) { return analyzer.analyze(AnalysisContext.builder().currentIndicator(current).indicatorHistory(history).build()); }
    private DailyIndicator bullish(LocalDate date, String histogram) { return DailyIndicator.builder().tradingDate(date).rsi14(BigDecimal.valueOf(60)).macd(BigDecimal.valueOf(2)).macdSignal(BigDecimal.ONE).macdHistogram(new BigDecimal(histogram)).stochasticK(BigDecimal.valueOf(65)).stochasticD(BigDecimal.valueOf(55)).mfi(BigDecimal.valueOf(60)).build(); }
}

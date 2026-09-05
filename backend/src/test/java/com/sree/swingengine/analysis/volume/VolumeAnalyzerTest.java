package com.sree.swingengine.analysis.volume;

import com.sree.swingengine.analysis.common.AnalysisContext;
import com.sree.swingengine.entity.DailyIndicator;
import com.sree.swingengine.entity.DailyPrice;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VolumeAnalyzerTest {
    private final VolumeAnalyzer analyzer = new VolumeAnalyzer();

    @Test void awardsAllPointsForConfirmedInstitutionalBuying() {
        DailyPrice previous = price(LocalDate.of(2026, 9, 3), "100", 800);
        DailyPrice current = price(LocalDate.of(2026, 9, 4), "105", 2000);
        DailyIndicator previousIndicator = indicator(LocalDate.of(2026, 9, 3), "1000");
        DailyIndicator currentIndicator = indicator(LocalDate.of(2026, 9, 4), "1200");
        VolumeAnalysisResult result = analyze(current, currentIndicator, List.of(previous, current), List.of(previousIndicator, currentIndicator));
        assertThat(result.getScore()).isEqualTo(100);
        assertThat(result.isBullish()).isTrue();
        assertThat(result.getStrength()).isEqualTo(VolumeStrength.VERY_STRONG);
    }

    @Test void usesSavedRelativeVolumeWithoutPriceAndFallsBackWhenMissing() {
        DailyIndicator saved = indicator(LocalDate.of(2026, 9, 4), "1200");
        VolumeAnalysisResult savedResult = analyze(null, saved, null, null);
        assertThat(savedResult.getScoreBreakdown()).containsEntry("Relative volume", 40);

        DailyIndicator fallback = indicator(LocalDate.of(2026, 9, 4), "1200"); fallback.setRelativeVolume(null);
        VolumeAnalysisResult fallbackResult = analyze(price(LocalDate.of(2026, 9, 4), "105", 1500), fallback, null, null);
        assertThat(fallbackResult.getScoreBreakdown()).containsEntry("Relative volume", 30);
    }

    @Test void appliesRelativeVolumeBoundaries() {
        assertThat(relativeVolume(".9999")).isZero();
        assertThat(relativeVolume("1")).isEqualTo(10);
        assertThat(relativeVolume("1.2")).isEqualTo(20);
        assertThat(relativeVolume("1.5")).isEqualTo(30);
        assertThat(relativeVolume("2")).isEqualTo(40);
    }

    @Test void awardsObvOnlyWhenItRisesFromLatestEarlierSession() {
        DailyIndicator old = indicator(LocalDate.of(2026, 9, 1), "1000");
        DailyIndicator latestEarlier = indicator(LocalDate.of(2026, 9, 3), "1300");
        DailyIndicator current = indicator(LocalDate.of(2026, 9, 4), "1200");
        assertThat(analyze(null, current, null, List.of(old, current, latestEarlier)).getScoreBreakdown()).doesNotContainKey("OBV");
        current.setObv(BigDecimal.valueOf(1400));
        assertThat(analyze(null, current, null, List.of(old, current, latestEarlier)).getScoreBreakdown()).containsEntry("OBV", 25);
    }

    @Test void confirmsPriceOnlyWhenItRisesOnStrictlyAboveAverageVolume() {
        DailyPrice previous = price(LocalDate.of(2026, 9, 3), "100", 800);
        DailyIndicator indicator = indicator(LocalDate.of(2026, 9, 4), "1000");
        indicator.setRelativeVolume(null); indicator.setMfi(null); indicator.setObv(null);
        DailyPrice equalVolume = price(LocalDate.of(2026, 9, 4), "101", 1000);
        DailyPrice falling = price(LocalDate.of(2026, 9, 4), "99", 1500);
        DailyPrice confirmed = price(LocalDate.of(2026, 9, 4), "101", 1500);
        assertThat(analyze(equalVolume, indicator, List.of(previous, equalVolume), null).getScore()).isEqualTo(10);
        assertThat(analyze(falling, indicator, List.of(previous, falling), null).getScore()).isEqualTo(30);
        assertThat(analyze(confirmed, indicator, List.of(previous, confirmed), null).getScore()).isEqualTo(50);
    }

    @Test void appliesMfiBoundariesAndHandlesMissingValuesAndZeroSmaSafely() {
        assertThat(mfi("39.99")).isZero(); assertThat(mfi("40")).isEqualTo(8); assertThat(mfi("50")).isEqualTo(15); assertThat(mfi("80")).isEqualTo(6); assertThat(mfi("90")).isZero();
        DailyIndicator missing = DailyIndicator.builder().tradingDate(LocalDate.now()).volumeSma20(BigDecimal.ZERO).build();
        assertThat(analyze(price(LocalDate.now(), "100", 1000), missing, null, null).getScore()).isZero();
    }

    @Test void rejectsMissingCurrentIndicator() {
        assertThatThrownBy(() -> analyzer.analyze(AnalysisContext.builder().build())).isInstanceOf(IllegalArgumentException.class).hasMessage("Analysis context must contain a current indicator");
    }

    @Test void ignoresNullAndSameDayHistoryAndUsesLatestStrictlyEarlierValues() {
        DailyPrice currentPrice = price(LocalDate.of(2026, 9, 4), "105", 1500);
        DailyPrice sameDay = price(LocalDate.of(2026, 9, 4), "90", 500);
        DailyPrice earlier = price(LocalDate.of(2026, 9, 3), "100", 800);
        DailyIndicator current = indicator(LocalDate.of(2026, 9, 4), "1200");
        DailyIndicator sameDayIndicator = indicator(LocalDate.of(2026, 9, 4), "500");
        DailyIndicator earlierIndicator = indicator(LocalDate.of(2026, 9, 3), "1000");
        VolumeAnalysisResult result = analyze(currentPrice, current,
                java.util.Arrays.asList(null, sameDay, earlier, currentPrice),
                java.util.Arrays.asList(null, sameDayIndicator, earlierIndicator, current));
        assertThat(result.getScoreBreakdown()).containsEntry("OBV", 25)
                .containsEntry("Price-volume confirmation", 20);
    }

    @Test void doesNotUseFallbackForZeroOrNegativeAverageVolume() {
        DailyPrice current = price(LocalDate.of(2026, 9, 4), "105", 1500);
        DailyIndicator zero = DailyIndicator.builder().tradingDate(current.getTradingDate()).volumeSma20(BigDecimal.ZERO).build();
        DailyIndicator negative = DailyIndicator.builder().tradingDate(current.getTradingDate()).volumeSma20(BigDecimal.valueOf(-1)).build();
        assertThat(analyze(current, zero, null, null).getScore()).isZero();
        assertThat(analyze(current, negative, null, null).getScore()).isZero();
    }

    private int relativeVolume(String value) { return analyze(null, DailyIndicator.builder().tradingDate(LocalDate.now()).relativeVolume(new BigDecimal(value)).build(), null, null).getScore(); }
    private int mfi(String value) { return analyze(null, DailyIndicator.builder().tradingDate(LocalDate.now()).mfi(new BigDecimal(value)).build(), null, null).getScore(); }
    private VolumeAnalysisResult analyze(DailyPrice price, DailyIndicator indicator, List<DailyPrice> prices, List<DailyIndicator> indicators) { return analyzer.analyze(AnalysisContext.builder().currentPrice(price).currentIndicator(indicator).priceHistory(prices).indicatorHistory(indicators).build()); }
    private DailyPrice price(LocalDate date, String close, long volume) { return DailyPrice.builder().tradingDate(date).close(new BigDecimal(close)).volume(volume).build(); }
    private DailyIndicator indicator(LocalDate date, String obv) { return DailyIndicator.builder().tradingDate(date).relativeVolume(BigDecimal.valueOf(2)).volumeSma20(BigDecimal.valueOf(1000)).obv(new BigDecimal(obv)).mfi(BigDecimal.valueOf(60)).build(); }
}

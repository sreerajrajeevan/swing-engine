package com.sree.swingengine.analysis.overall;

import com.sree.swingengine.analysis.momentum.MomentumAnalysisResult;
import com.sree.swingengine.analysis.trend.TrendAnalysisResult;
import com.sree.swingengine.analysis.volume.VolumeAnalysisResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OverallScoreCalculatorTest {

    private final OverallScoreCalculator calculator = new OverallScoreCalculator();

    @Test
    void calculatesTheIntendedPreRiskWeightsWithoutInventingRisk() {
        OverallScoreResult result = calculator.calculate(trend(80), momentum(60), volume(40));

        assertThat(result.getWeightedBreakdown())
                .containsEntry("Trend", 28.0)
                .containsEntry("Momentum", 15.0)
                .containsEntry("Volume", 10.0)
                .doesNotContainKey("Risk");
        assertThat(result.getProvisionalScore()).isEqualTo(53.0);
        assertThat(result.getProvisionalScoreNormalized()).isEqualTo(62.35);
        assertThat(result.getFinalScore()).isNull();
        assertThat(result.getRiskScore()).isNull();
        assertThat(result.getReasons()).anyMatch(reason -> reason.contains("Risk score unavailable"));
    }

    @Test
    void hasAnEightyFivePointMaximumBeforeRiskAndNormalizesItForClassification() {
        OverallScoreResult result = calculator.calculate(trend(100), momentum(100), volume(100));

        assertThat(result.getProvisionalScore()).isEqualTo(85.0);
        assertThat(result.getProvisionalScoreNormalized()).isEqualTo(100.0);
        assertThat(result.getFinalScore()).isNull();
        assertThat(result.getStrength()).isEqualTo(OverallScoreStrength.VERY_STRONG);
    }

    @Test
    void clampsOutOfRangeScoresAndclassifiesExactBoundaries() {
        OverallScoreResult low = calculator.calculate(trend(-10), momentum(0), volume(0));
        OverallScoreResult strong = calculator.calculate(trend(100), momentum(100), volume(38));
        OverallScoreResult capped = calculator.calculate(trend(101), momentum(200), volume(100));

        assertThat(low.getProvisionalScore()).isZero();
        assertThat(low.getStrength()).isEqualTo(OverallScoreStrength.VERY_WEAK);
        assertThat(strong.getProvisionalScoreNormalized()).isEqualTo(70.0);
        assertThat(strong.getStrength()).isEqualTo(OverallScoreStrength.STRONG);
        assertThat(capped.getProvisionalScore()).isEqualTo(85.0);
        assertThat(capped.getTrendScore()).isEqualTo(100);
        assertThat(capped.getMomentumScore()).isEqualTo(100);
    }

    @Test
    void handlesMissingAnalyzerResultsAsUnavailableRatherThanZeroScores() {
        OverallScoreResult result = calculator.calculate(null, momentum(80), null);

        assertThat(result.getTrendScore()).isNull();
        assertThat(result.getVolumeScore()).isNull();
        assertThat(result.getProvisionalScore()).isEqualTo(20.0);
        assertThat(result.getWeightedBreakdown()).containsOnlyKeys("Momentum");
        assertThat(result.getReasons()).anyMatch(reason -> reason.equals("Trend analysis unavailable"))
                .anyMatch(reason -> reason.equals("Volume analysis unavailable"));
    }

    @Test
    void producesACompleteFinalScoreOnlyWhenFutureRiskScoreIsProvided() {
        OverallScoreResult result = calculator.calculate(trend(80), momentum(60), volume(40), 50);

        assertThat(result.getWeightedBreakdown()).containsEntry("Risk", 7.5);
        assertThat(result.getProvisionalScore()).isEqualTo(53.0);
        assertThat(result.getFinalScore()).isEqualTo(60.5);
        assertThat(result.getRiskScore()).isEqualTo(50);
        assertThat(result.getStrength()).isEqualTo(OverallScoreStrength.NEUTRAL);
    }

    private TrendAnalysisResult trend(int score) { return TrendAnalysisResult.builder().score(score).build(); }
    private MomentumAnalysisResult momentum(int score) { return MomentumAnalysisResult.builder().score(score).build(); }
    private VolumeAnalysisResult volume(int score) { return VolumeAnalysisResult.builder().score(score).build(); }
}

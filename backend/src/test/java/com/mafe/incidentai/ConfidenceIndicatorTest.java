package com.mafe.incidentai;

import com.mafe.incidentai.dto.AiQueryHistoryResponseDTO.ConfidenceIndicatorDTO;
import com.mafe.incidentai.service.ConfidenceIndicator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConfidenceIndicatorTest {

    private static final double THRESHOLD = 0.55;

    @Test
    void threeHighSimilaritySourcesGiveHighLevel() {
        ConfidenceIndicatorDTO result = ConfidenceIndicator.calculate(List.of(0.91, 0.88, 0.85), THRESHOLD);

        assertThat(result.usedSources()).isEqualTo(3);
        assertThat(result.level()).isEqualTo("ALTO");
        assertThat(result.sufficientEvidence()).isTrue();
    }

    @Test
    void oneSourceNearThresholdGivesLowLevel() {
        ConfidenceIndicatorDTO result = ConfidenceIndicator.calculate(List.of(0.56), THRESHOLD);

        assertThat(result.usedSources()).isEqualTo(1);
        assertThat(result.index()).isEqualTo(12);
        assertThat(result.level()).isEqualTo("BAJO");
    }

    @Test
    void formulaIsMathematicallyCorrect() {
        // promedio = 0.88, (0.88-0.55)/0.45 = 0.7333, 100*(0.7*0.7333 + 0.3*3/3) = 81.33 -> 81
        assertThat(ConfidenceIndicator.calculate(List.of(0.91, 0.88, 0.85), THRESHOLD).index()).isEqualTo(81);
        // promedio = 0.75, (0.75-0.55)/0.45 = 0.4444, 100*(0.7*0.4444 + 0.3*2/3) = 51.11 -> 51
        ConfidenceIndicatorDTO medium = ConfidenceIndicator.calculate(List.of(0.80, 0.70), THRESHOLD);
        assertThat(medium.index()).isEqualTo(51);
        assertThat(medium.level()).isEqualTo("MEDIO");
        assertThat(medium.averageSimilarity()).isEqualTo(0.75);
        // la fuente 0.40 queda bajo el umbral y no se usa
        assertThat(ConfidenceIndicator.calculate(List.of(0.80, 0.70, 0.40), THRESHOLD).index()).isEqualTo(51);
        // 5 fuentes perfectas no pasan de 100
        assertThat(ConfidenceIndicator.calculate(List.of(1.0, 1.0, 1.0, 1.0, 1.0), THRESHOLD).index()).isEqualTo(100);
    }

    @Test
    void noSourcesMeansInsufficientEvidence() {
        List<Double> withNull = new ArrayList<>();
        withNull.add(null);

        for (List<Double> scores : List.of(List.<Double>of(), List.of(0.30, 0.50), withNull)) {
            ConfidenceIndicatorDTO result = ConfidenceIndicator.calculate(scores, THRESHOLD);
            assertThat(result.index()).isZero();
            assertThat(result.level()).isEqualTo("BAJO");
            assertThat(result.sufficientEvidence()).isFalse();
        }
        assertThat(ConfidenceIndicator.calculate(null, THRESHOLD).sufficientEvidence()).isFalse();
    }
}

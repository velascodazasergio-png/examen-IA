package com.mafe.incidentai.service;

import com.mafe.incidentai.dto.AiQueryHistoryResponseDTO.ConfidenceIndicatorDTO;

import java.util.List;
import java.util.Objects;

// indice = 100 * (0.7 * (promedio - umbral) / (1 - umbral) + 0.3 * min(fuentes, 3) / 3)
public final class ConfidenceIndicator {

    private ConfidenceIndicator() {}

    public static ConfidenceIndicatorDTO calculate(List<Double> scores, double threshold) {
        List<Double> used = scores == null ? List.of() : scores.stream()
                .filter(Objects::nonNull)
                .filter(s -> s >= threshold)
                .toList();

        if (used.isEmpty()) {
            return new ConfidenceIndicatorDTO(0, "BAJO", 0, 0.0, false);
        }

        double average = used.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double similarityPart = Math.min(1, Math.max(0, (average - threshold) / (1 - threshold)));
        double sourcesPart = Math.min(used.size(), 3) / 3.0;
        int index = (int) Math.round(100 * (0.7 * similarityPart + 0.3 * sourcesPart));

        String level = index >= 70 ? "ALTO" : index >= 40 ? "MEDIO" : "BAJO";
        return new ConfidenceIndicatorDTO(index, level, used.size(), Math.round(average * 10000) / 10000.0, true);
    }
}

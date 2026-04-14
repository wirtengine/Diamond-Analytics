package com.tumundomlb.moundmetrics.service;

import com.tumundomlb.moundmetrics.dto.FieldingStatsDTO;
import com.tumundomlb.moundmetrics.entity.FieldingAppearance;
import com.tumundomlb.moundmetrics.entity.Fielder;
import com.tumundomlb.moundmetrics.repository.FielderRepository;
import com.tumundomlb.moundmetrics.repository.FieldingAppearanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FieldingStatsService {

    private final FieldingAppearanceRepository appearanceRepository;
    private final FielderRepository fielderRepository;

    private static final LocalDate SEASON_START = LocalDate.of(2026, 3, 26);

    public FieldingStatsDTO getFieldingStats(Integer fielderId, int daysBack) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(daysBack);
        if (start.isBefore(SEASON_START)) {
            start = SEASON_START;
        }

        List<FieldingAppearance> appearances = appearanceRepository
                .findByFielderIdAndGameDateBetweenOrderByGameDateDesc(fielderId, start, end);

        Fielder fielder = fielderRepository.findById(fielderId).orElse(null);
        String fullName = fielder != null ? fielder.getFullName() : "Desconocido";
        String primaryPosition = fielder != null ? fielder.getPrimaryPosition() : "";

        // Filtrar solo apariciones con participación defensiva real (PO+A+E > 0)
        List<FieldingAppearance> validas = appearances.stream()
                .filter(a -> (a.getPutOuts() != null ? a.getPutOuts() : 0) +
                        (a.getAssists() != null ? a.getAssists() : 0) +
                        (a.getErrors() != null ? a.getErrors() : 0) > 0)
                .toList();

        if (validas.isEmpty()) {
            return FieldingStatsDTO.builder()
                    .fielderId(fielderId)
                    .fullName(fullName)
                    .primaryPosition(primaryPosition)
                    .gamesPlayed(0)
                    .gamesStarted(0)
                    .putOuts(0)
                    .assists(0)
                    .errors(0)
                    .doublePlays(0)
                    .triplePlays(0)
                    .fieldingPercentage(BigDecimal.ZERO)
                    .build();
        }

        int totalPO = 0, totalA = 0, totalE = 0, totalDP = 0, totalTP = 0;
        int totalG = 0, totalGS = 0;

        for (FieldingAppearance fa : validas) {
            totalPO += fa.getPutOuts() != null ? fa.getPutOuts() : 0;
            totalA += fa.getAssists() != null ? fa.getAssists() : 0;
            totalE += fa.getErrors() != null ? fa.getErrors() : 0;
            totalDP += fa.getDoublePlays() != null ? fa.getDoublePlays() : 0;
            totalTP += fa.getTriplePlays() != null ? fa.getTriplePlays() : 0;
            totalG += fa.getGamesPlayed() != null ? fa.getGamesPlayed() : 1;
            totalGS += fa.getGamesStarted() != null ? fa.getGamesStarted() : 0;
        }

        BigDecimal fieldingPct = BigDecimal.ZERO;
        int denominator = totalPO + totalA + totalE;
        if (denominator > 0) {
            fieldingPct = BigDecimal.valueOf(totalPO + totalA)
                    .divide(BigDecimal.valueOf(denominator), 3, RoundingMode.HALF_UP);
        }

        log.info("🧤 Filder {}: PO={}, A={}, E={}, FPCT={}", fullName, totalPO, totalA, totalE, fieldingPct);

        return FieldingStatsDTO.builder()
                .fielderId(fielderId)
                .fullName(fullName)
                .primaryPosition(primaryPosition)
                .gamesPlayed(totalG)
                .gamesStarted(totalGS)
                .putOuts(totalPO)
                .assists(totalA)
                .errors(totalE)
                .doublePlays(totalDP)
                .triplePlays(totalTP)
                .fieldingPercentage(fieldingPct)
                .build();
    }
}
package com.tumundomlb.moundmetrics.service;

import com.tumundomlb.moundmetrics.dto.BullpenStatsDTO;
import com.tumundomlb.moundmetrics.entity.PitchingAppearance;
import com.tumundomlb.moundmetrics.repository.PitchingAppearanceRepository;
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
public class BullpenStatsService {

    private final PitchingAppearanceRepository appearanceRepository;

    public BullpenStatsDTO getTeamBullpenStats(Integer teamId, LocalDate fromDate, LocalDate toDate) {

        log.info("Calculando bullpen para equipo {} desde {} hasta {}", teamId, fromDate, toDate);

        // 🔥 PRIORIDAD: usar primary_position = RP
        List<PitchingAppearance> appearances = appearanceRepository
                .findTeamBullpenAppearances(teamId, "RP", fromDate, toDate);

        log.info("Apariciones con primary_position='RP': {}", appearances.size());

        // 🔥 FALLBACK: usar starter = false
        if (appearances.isEmpty()) {
            log.warn("No se encontraron RP. Aplicando fallback con starter=false...");

            List<PitchingAppearance> allAppearances =
                    appearanceRepository.findAllTeamAppearances(teamId, fromDate, toDate);

            appearances = allAppearances.stream()
                    .filter(app -> !Boolean.TRUE.equals(app.isStarter()))
                    .toList();

            log.info("Total apariciones: {} | Relevistas detectados: {}",
                    allAppearances.size(), appearances.size());
        }

        // 🔥 CASO SIN DATOS
        if (appearances.isEmpty()) {
            log.warn("No hay datos de bullpen para equipo {}", teamId);

            return BullpenStatsDTO.builder()
                    .teamId(teamId)
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .totalInnings(BigDecimal.ZERO)
                    .earnedRuns(0)
                    .era(BigDecimal.ZERO)
                    .whip(BigDecimal.ZERO)
                    .appearancesCount(0)
                    .build();
        }

        // 🔥 ACUMULADORES
        int totalEarnedRuns = 0;
        BigDecimal totalInnings = BigDecimal.ZERO;
        int totalWalks = 0;
        int totalHits = 0;

        for (PitchingAppearance app : appearances) {
            totalEarnedRuns += app.getEarnedRuns() != null ? app.getEarnedRuns() : 0;

            totalInnings = totalInnings.add(
                    app.getInningsPitched() != null ? app.getInningsPitched() : BigDecimal.ZERO
            );

            totalWalks += app.getBaseOnBalls() != null ? app.getBaseOnBalls() : 0;
            totalHits += app.getHits() != null ? app.getHits() : 0;
        }

        // 🔥 ERA
        BigDecimal era = BigDecimal.ZERO;
        if (totalInnings.compareTo(BigDecimal.ZERO) > 0) {
            era = BigDecimal.valueOf(totalEarnedRuns * 9.0)
                    .divide(totalInnings, 2, RoundingMode.HALF_UP);
        }

        // 🔥 WHIP
        BigDecimal whip = BigDecimal.ZERO;
        if (totalInnings.compareTo(BigDecimal.ZERO) > 0) {
            whip = BigDecimal.valueOf(totalWalks + totalHits)
                    .divide(totalInnings, 3, RoundingMode.HALF_UP);
        }

        log.info("Bullpen calculado -> ERA: {} | WHIP: {} | IP: {} | Apariciones: {}",
                era, whip, totalInnings, appearances.size());

        return BullpenStatsDTO.builder()
                .teamId(teamId)
                .fromDate(fromDate)
                .toDate(toDate)
                .totalInnings(totalInnings)
                .earnedRuns(totalEarnedRuns)
                .era(era)
                .whip(whip)
                .appearancesCount(appearances.size())
                .build();
    }
}
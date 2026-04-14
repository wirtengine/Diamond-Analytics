package com.tumundomlb.moundmetrics.service;

import com.tumundomlb.moundmetrics.dto.PitcherStatsDTO;
import com.tumundomlb.moundmetrics.entity.PitchingAppearance;
import com.tumundomlb.moundmetrics.entity.Pitcher;
import com.tumundomlb.moundmetrics.repository.PitcherRepository;
import com.tumundomlb.moundmetrics.repository.PitchingAppearanceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PitcherStatsService {

    private static final Logger log = LoggerFactory.getLogger(PitcherStatsService.class);

    private final PitchingAppearanceRepository appearanceRepository;
    private final PitcherRepository pitcherRepository;

    // ✅ NUEVO: Inicio de temporada 2026 (sin borrar nada del código original)
    private static final LocalDate SEASON_START = LocalDate.of(2026, 3, 26);

    public PitcherStatsDTO getPitcherStats(Integer pitcherId, int daysBack) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(daysBack);

        // 🔥 NUEVO: Ajuste para no empezar antes del inicio de temporada
        if (start.isBefore(SEASON_START)) {
            start = SEASON_START;
        }

        List<PitchingAppearance> appearances = appearanceRepository
                .findByPitcherIdAndGameDateBetweenOrderByGameDateDesc(pitcherId, start, end);

        Pitcher pitcher = pitcherRepository.findById(pitcherId).orElse(null);
        String fullName = pitcher != null ? pitcher.getFullName() : "Desconocido";

        if (appearances.isEmpty()) {
            return PitcherStatsDTO.builder()
                    .pitcherId(pitcherId)
                    .fullName(fullName)
                    .gamesPlayed(0)
                    .era(BigDecimal.ZERO)
                    .whip(BigDecimal.ZERO)
                    .inningsPitched(BigDecimal.ZERO)
                    .strikeOuts(0)
                    .baseOnBalls(0)
                    .hits(0)
                    .earnedRuns(0)
                    .kPer9(BigDecimal.ZERO)
                    .bbPer9(BigDecimal.ZERO)
                    .build();
        }

        // 🔥 FILTRO: solo apariciones reales
        List<PitchingAppearance> appearancesValidas = appearances.stream()
                .filter(app -> app.getPitchesThrown() != null && app.getPitchesThrown() > 0)
                .toList();

        if (appearancesValidas.isEmpty()) {
            return PitcherStatsDTO.builder()
                    .pitcherId(pitcherId)
                    .fullName(fullName)
                    .gamesPlayed(0)
                    .era(BigDecimal.ZERO)
                    .whip(BigDecimal.ZERO)
                    .inningsPitched(BigDecimal.ZERO)
                    .strikeOuts(0)
                    .baseOnBalls(0)
                    .hits(0)
                    .earnedRuns(0)
                    .kPer9(BigDecimal.ZERO)
                    .bbPer9(BigDecimal.ZERO)
                    .build();
        }

        int totalEarnedRuns = 0;
        BigDecimal totalInnings = BigDecimal.ZERO;
        int totalWalks = 0;
        int totalHits = 0;
        int totalStrikeOuts = 0;

        for (PitchingAppearance app : appearancesValidas) {
            totalEarnedRuns += app.getEarnedRuns() != null ? app.getEarnedRuns() : 0;
            totalInnings = totalInnings.add(
                    app.getInningsPitched() != null ? app.getInningsPitched() : BigDecimal.ZERO
            );
            totalWalks += app.getBaseOnBalls() != null ? app.getBaseOnBalls() : 0;
            totalHits += app.getHits() != null ? app.getHits() : 0;
            totalStrikeOuts += app.getStrikeOuts() != null ? app.getStrikeOuts() : 0;
        }

        BigDecimal era = BigDecimal.ZERO;
        if (totalInnings.compareTo(BigDecimal.ZERO) > 0) {
            era = BigDecimal.valueOf(totalEarnedRuns)
                    .multiply(BigDecimal.valueOf(9))
                    .divide(totalInnings, 2, RoundingMode.HALF_UP);
        }

        BigDecimal whip = BigDecimal.ZERO;
        if (totalInnings.compareTo(BigDecimal.ZERO) > 0) {
            whip = BigDecimal.valueOf(totalWalks + totalHits)
                    .divide(totalInnings, 3, RoundingMode.HALF_UP);
        }

        BigDecimal kPer9 = BigDecimal.ZERO;
        BigDecimal bbPer9 = BigDecimal.ZERO;

        if (totalInnings.compareTo(BigDecimal.ZERO) > 0) {
            kPer9 = BigDecimal.valueOf(totalStrikeOuts)
                    .multiply(BigDecimal.valueOf(9))
                    .divide(totalInnings, 2, RoundingMode.HALF_UP);

            bbPer9 = BigDecimal.valueOf(totalWalks)
                    .multiply(BigDecimal.valueOf(9))
                    .divide(totalInnings, 2, RoundingMode.HALF_UP);
        }

        // 🔍 LOG PARA DEBUG (original tuyo, sin cambios)
        log.info("🧮 Pitcher {}: ER={}, IP={}, SO={}, BB={}, K/9={}",
                pitcherId, totalEarnedRuns, totalInnings, totalStrikeOuts, totalWalks, kPer9);

        return PitcherStatsDTO.builder()
                .pitcherId(pitcherId)
                .fullName(fullName)
                .gamesPlayed(appearancesValidas.size())
                .era(era)
                .whip(whip)
                .inningsPitched(totalInnings)
                .strikeOuts(totalStrikeOuts)
                .baseOnBalls(totalWalks)
                .hits(totalHits)
                .earnedRuns(totalEarnedRuns)
                .kPer9(kPer9)
                .bbPer9(bbPer9)
                .build();
    }
}
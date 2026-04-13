package com.tumundomlb.moundmetrics.service;

import com.tumundomlb.moundmetrics.dto.BatterStatsDTO;
import com.tumundomlb.moundmetrics.entity.BattingAppearance;
import com.tumundomlb.moundmetrics.entity.Batter;
import com.tumundomlb.moundmetrics.repository.BatterRepository;
import com.tumundomlb.moundmetrics.repository.BattingAppearanceRepository;
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
public class BatterStatsService {

    private final BattingAppearanceRepository appearanceRepository;
    private final BatterRepository batterRepository;

    private static final LocalDate SEASON_START = LocalDate.of(2026, 3, 26);

    public BatterStatsDTO getBatterStats(Integer batterId, int daysBack) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(daysBack);

        // 🔥 No empezar antes del inicio de temporada
        if (start.isBefore(SEASON_START)) {
            start = SEASON_START;
        }

        List<BattingAppearance> appearances = appearanceRepository
                .findByBatterIdAndGameDateBetweenOrderByGameDateDesc(batterId, start, end);

        Batter batter = batterRepository.findById(batterId).orElse(null);
        String fullName = batter != null ? batter.getFullName() : "Desconocido";

        // Filtrar solo apariciones con al menos un turno
        List<BattingAppearance> validas = appearances.stream()
                .filter(a -> a.getAtBats() != null && a.getAtBats() > 0)
                .toList();

        if (validas.isEmpty()) {
            return BatterStatsDTO.builder()
                    .batterId(batterId)
                    .fullName(fullName)
                    .gamesPlayed(0)
                    .atBats(0).runs(0).hits(0).doubles(0).triples(0).homeRuns(0)
                    .rbi(0).baseOnBalls(0).strikeOuts(0).stolenBases(0)
                    .avg(BigDecimal.ZERO).obp(BigDecimal.ZERO).slg(BigDecimal.ZERO).ops(BigDecimal.ZERO)
                    .build();
        }

        int totalAB = 0, totalR = 0, totalH = 0, total2B = 0, total3B = 0, totalHR = 0;
        int totalRBI = 0, totalBB = 0, totalSO = 0, totalSB = 0;
        int totalHBP = 0, totalSF = 0;

        for (BattingAppearance ba : validas) {
            totalAB += ba.getAtBats() != null ? ba.getAtBats() : 0;
            totalR += ba.getRuns() != null ? ba.getRuns() : 0;
            totalH += ba.getHits() != null ? ba.getHits() : 0;
            total2B += ba.getDoubles() != null ? ba.getDoubles() : 0;
            total3B += ba.getTriples() != null ? ba.getTriples() : 0;
            totalHR += ba.getHomeRuns() != null ? ba.getHomeRuns() : 0;
            totalRBI += ba.getRbi() != null ? ba.getRbi() : 0;
            totalBB += ba.getBaseOnBalls() != null ? ba.getBaseOnBalls() : 0;
            totalSO += ba.getStrikeOuts() != null ? ba.getStrikeOuts() : 0;
            totalSB += ba.getStolenBases() != null ? ba.getStolenBases() : 0;
            totalHBP += ba.getHitByPitch() != null ? ba.getHitByPitch() : 0;
            totalSF += ba.getSacFlies() != null ? ba.getSacFlies() : 0;
        }

        BigDecimal avg = totalAB > 0 ?
                BigDecimal.valueOf(totalH).divide(BigDecimal.valueOf(totalAB), 3, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        int obpNumerator = totalH + totalBB + totalHBP;
        int obpDenominator = totalAB + totalBB + totalHBP + totalSF;
        BigDecimal obp = obpDenominator > 0 ?
                BigDecimal.valueOf(obpNumerator).divide(BigDecimal.valueOf(obpDenominator), 3, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        int totalBases = totalH + total2B + (total3B * 2) + (totalHR * 3);
        BigDecimal slg = totalAB > 0 ?
                BigDecimal.valueOf(totalBases).divide(BigDecimal.valueOf(totalAB), 3, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        BigDecimal ops = obp.add(slg);

        log.info("🧮 Bateador {} ({}): Juegos={}, AB={}, H={}, HR={}, AVG={}, OPS={}",
                fullName, batterId, validas.size(), totalAB, totalH, totalHR, avg, ops);

        return BatterStatsDTO.builder()
                .batterId(batterId)
                .fullName(fullName)
                .gamesPlayed(validas.size())
                .atBats(totalAB)
                .runs(totalR)
                .hits(totalH)
                .doubles(total2B)
                .triples(total3B)
                .homeRuns(totalHR)
                .rbi(totalRBI)
                .baseOnBalls(totalBB)
                .strikeOuts(totalSO)
                .stolenBases(totalSB)
                .avg(avg)
                .obp(obp)
                .slg(slg)
                .ops(ops)
                .build();
    }
}
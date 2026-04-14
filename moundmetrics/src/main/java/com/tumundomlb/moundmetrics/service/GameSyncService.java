package com.tumundomlb.moundmetrics.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tumundomlb.moundmetrics.entity.*;
import com.tumundomlb.moundmetrics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncService {

    private final MlbApiClient mlbApiClient;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final PitcherRepository pitcherRepository;
    private final PitchingAppearanceRepository appearanceRepository;
    private final BatterRepository batterRepository;
    private final BattingAppearanceRepository battingAppearanceRepository;

    // ✅ NUEVO
    private final FielderRepository fielderRepository;
    private final FieldingAppearanceRepository fieldingAppearanceRepository;

    @Transactional
    public void syncGamesForDate(LocalDate date) {
        log.info("Iniciando sincronización para la fecha: {}", date);

        JsonNode scheduleRoot = mlbApiClient.getSchedule(date);
        JsonNode dates = scheduleRoot.path("dates");

        if (dates.isEmpty()) {
            log.info("No se encontraron juegos para {}", date);
            return;
        }

        JsonNode games = dates.get(0).path("games");
        log.info("Se encontraron {} juegos en el schedule", games.size());

        for (JsonNode gameNode : games) {
            Long gamePk = gameNode.path("gamePk").asLong();
            String status = gameNode.path("status").path("detailedState").asText();

            if (!"Final".equals(status)) {
                log.info("Omitiendo juego {} - estado: {}", gamePk, status);
                continue;
            }

            try {
                JsonNode liveData = mlbApiClient.getLiveGame(gamePk);

                if (liveData == null) {
                    log.warn("No se pudo obtener liveData para gamePk {}", gamePk);
                    continue;
                }

                Game game = persistGame(gameNode, liveData);
                persistPitchingAppearances(game, liveData);
                persistBattingAppearances(game, liveData);

                // ✅ FILDEO
                persistFieldingAppearances(game, liveData);

                log.info("Juego {} sincronizado correctamente", gamePk);

            } catch (Exception e) {
                log.error("Error al procesar el juego {}: {}", gamePk, e.getMessage(), e);
            }
        }

        log.info("Sincronización completada para {}", date);
    }

    private Game persistGame(JsonNode scheduleGame, JsonNode liveData) {
        Long gamePk = scheduleGame.path("gamePk").asLong();

        Game game = gameRepository.findById(gamePk)
                .orElse(Game.builder().id(gamePk).build());

        game.setOfficialDate(LocalDate.parse(scheduleGame.path("officialDate").asText()));

        String gameDateStr = scheduleGame.path("gameDate").asText();
        if (!gameDateStr.isEmpty()) {
            game.setGameDateTime(OffsetDateTime.parse(gameDateStr).toLocalDateTime());
        }

        JsonNode teams = scheduleGame.path("teams");
        Integer homeId = teams.path("home").path("team").path("id").asInt();
        Integer awayId = teams.path("away").path("team").path("id").asInt();

        game.setHomeTeam(teamRepository.getReferenceById(homeId));
        game.setAwayTeam(teamRepository.getReferenceById(awayId));

        JsonNode linescore = liveData.path("liveData").path("linescore");
        game.setHomeScore(linescore.path("teams").path("home").path("runs").asInt());
        game.setAwayScore(linescore.path("teams").path("away").path("runs").asInt());
        game.setStatus("Final");

        return gameRepository.save(game);
    }

    private void persistPitchingAppearances(Game game, JsonNode liveData) {
        JsonNode boxscore = liveData.path("liveData").path("boxscore");
        JsonNode teamsBox = boxscore.path("teams");

        for (String side : Arrays.asList("home", "away")) {
            JsonNode teamBox = teamsBox.path(side);
            if (teamBox.isMissingNode()) continue;

            JsonNode players = teamBox.path("players");
            if (!players.isMissingNode() && players.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> playerFields = players.fields();

                while (playerFields.hasNext()) {
                    JsonNode playerNode = playerFields.next().getValue();
                    JsonNode pitchingStats = playerNode.path("stats").path("pitching");
                    JsonNode battingStats = playerNode.path("stats").path("batting");

                    if (!pitchingStats.isMissingNode()) {
                        String pos = playerNode.path("position").path("abbreviation").asText();
                        boolean esPitcher = "P".equals(pos) || !pitchingStats.isMissingNode();
                        if (esPitcher) {
                            procesarNodoPitcher(game, teamBox, playerNode, pitchingStats);
                        }
                    }

                    if (!battingStats.isMissingNode()) {
                        procesarNodoBateador(game, teamBox, playerNode, battingStats);
                    }
                }
            } else {
                JsonNode pitchersNode = teamBox.path("pitchers");
                if (!pitchersNode.isMissingNode()) {
                    procesarNodoPitchersAntiguo(game, teamBox, pitchersNode);
                }
            }
        }
    }

    private void persistBattingAppearances(Game game, JsonNode liveData) {
        JsonNode boxscore = liveData.path("liveData").path("boxscore");
        JsonNode teamsBox = boxscore.path("teams");

        for (String side : Arrays.asList("home", "away")) {
            JsonNode teamBox = teamsBox.path(side);
            if (teamBox.isMissingNode()) continue;

            JsonNode players = teamBox.path("players");
            if (players.isMissingNode() || !players.isObject()) continue;

            Iterator<Map.Entry<String, JsonNode>> playerFields = players.fields();
            while (playerFields.hasNext()) {
                JsonNode playerNode = playerFields.next().getValue();
                JsonNode stats = playerNode.path("stats").path("batting");
                if (stats.isMissingNode()) continue;

                procesarNodoBateador(game, teamBox, playerNode, stats);
            }
        }
    }

    // ================= FILDEO =================

    private void persistFieldingAppearances(Game game, JsonNode liveData) {
        JsonNode boxscore = liveData.path("liveData").path("boxscore");
        JsonNode teamsBox = boxscore.path("teams");

        for (String side : Arrays.asList("home", "away")) {
            JsonNode teamBox = teamsBox.path(side);
            if (teamBox.isMissingNode()) continue;

            JsonNode players = teamBox.path("players");
            if (players.isMissingNode() || !players.isObject()) continue;

            Iterator<Map.Entry<String, JsonNode>> playerFields = players.fields();
            while (playerFields.hasNext()) {
                JsonNode playerNode = playerFields.next().getValue();
                JsonNode stats = playerNode.path("stats").path("fielding");
                if (stats.isMissingNode()) continue;

                if (getInt(stats, "po", "putOuts") == 0 &&
                        getInt(stats, "a", "assists") == 0 &&
                        getInt(stats, "e", "errors") == 0) {
                    continue;
                }

                procesarNodoFilder(game, teamBox, playerNode, stats);
            }
        }
    }

    private void procesarNodoFilder(Game game, JsonNode teamBox, JsonNode playerNode, JsonNode stats) {
        try {
            int fielderId = playerNode.path("person").path("id").asInt();
            String fullName = playerNode.path("person").path("fullName").asText();
            String position = playerNode.path("position").path("abbreviation").asText();

            Fielder fielder = fielderRepository.findById(fielderId).orElse(
                    Fielder.builder().id(fielderId).fullName(fullName).build()
            );

            Integer teamId = teamBox.path("team").path("id").asInt();
            fielder.setTeam(teamRepository.getReferenceById(teamId));
            fielder.setPrimaryPosition(position);
            fielder = fielderRepository.save(fielder);

            FieldingAppearance appearance = fieldingAppearanceRepository
                    .findByFielderIdAndGameId(fielderId, game.getId())
                    .orElse(FieldingAppearance.builder().fielder(fielder).game(game).build());

            appearance.setPutOuts(getInt(stats, "po", "putOuts"));
            appearance.setAssists(getInt(stats, "a", "assists"));
            appearance.setErrors(getInt(stats, "e", "errors"));
            appearance.setDoublePlays(getInt(stats, "dp", "doublePlays"));
            appearance.setTriplePlays(getInt(stats, "tp", "triplePlays"));
            appearance.setGamesPlayed(getInt(stats, "g", "gamesPlayed"));
            appearance.setGamesStarted(getInt(stats, "gs", "gamesStarted"));
            appearance.setPosition(position);
            appearance.setGameDate(game.getOfficialDate());

            fieldingAppearanceRepository.save(appearance);

        } catch (Exception e) {
            log.error("Error guardando filder en juego {}: {}", game.getId(), e.getMessage(), e);
        }
    }

    // ================= RESTO ORIGINAL =================

    private void procesarNodoBateador(Game game, JsonNode teamBox, JsonNode playerNode, JsonNode stats) {
        try {
            int batterId = playerNode.path("person").path("id").asInt();
            String fullName = playerNode.path("person").path("fullName").asText();

            Batter batter = batterRepository.findById(batterId).orElse(
                    Batter.builder().id(batterId).fullName(fullName).build()
            );

            Integer teamId = teamBox.path("team").path("id").asInt();
            batter.setTeam(teamRepository.getReferenceById(teamId));
            batter = batterRepository.save(batter);

            BattingAppearance appearance = battingAppearanceRepository
                    .findByBatterIdAndGameId(batterId, game.getId())
                    .orElse(BattingAppearance.builder().batter(batter).game(game).build());

            appearance.setAtBats(getInt(stats, "ab", "atBats"));
            appearance.setRuns(getInt(stats, "r", "runs"));
            appearance.setHits(getInt(stats, "h", "hits"));
            appearance.setDoubles(getInt(stats, "2b", "doubles"));
            appearance.setTriples(getInt(stats, "3b", "triples"));
            appearance.setHomeRuns(getInt(stats, "hr", "homeRuns"));
            appearance.setRbi(getInt(stats, "rbi"));
            appearance.setBaseOnBalls(getInt(stats, "bb", "baseOnBalls"));
            appearance.setIntentionalWalks(getInt(stats, "ibb", "intentionalWalks"));
            appearance.setStrikeOuts(getInt(stats, "so", "strikeOuts"));
            appearance.setStolenBases(getInt(stats, "sb", "stolenBases"));
            appearance.setCaughtStealing(getInt(stats, "cs", "caughtStealing"));
            appearance.setHitByPitch(getInt(stats, "hbp", "hitByPitch"));
            appearance.setSacFlies(getInt(stats, "sf", "sacFlies"));
            appearance.setSacBunts(getInt(stats, "sh", "sacBunts"));

            appearance.setAvg(calcularAvg(appearance));
            appearance.setObp(calcularObp(appearance));
            appearance.setSlg(calcularSlg(appearance));
            appearance.setOps(calcularOps(appearance));

            appearance.setGameDate(game.getOfficialDate());
            battingAppearanceRepository.save(appearance);

        } catch (Exception e) {
            log.error("Error guardando bateador en juego {}: {}", game.getId(), e.getMessage(), e);
        }
    }

    private BigDecimal calcularAvg(BattingAppearance ba) {
        if (ba.getAtBats() == null || ba.getAtBats() == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(ba.getHits() != null ? ba.getHits() : 0)
                .divide(BigDecimal.valueOf(ba.getAtBats()), 3, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularObp(BattingAppearance ba) {
        int numerador = (ba.getHits() != null ? ba.getHits() : 0)
                + (ba.getBaseOnBalls() != null ? ba.getBaseOnBalls() : 0)
                + (ba.getHitByPitch() != null ? ba.getHitByPitch() : 0);

        int denominador = (ba.getAtBats() != null ? ba.getAtBats() : 0)
                + (ba.getBaseOnBalls() != null ? ba.getBaseOnBalls() : 0)
                + (ba.getHitByPitch() != null ? ba.getHitByPitch() : 0)
                + (ba.getSacFlies() != null ? ba.getSacFlies() : 0);

        if (denominador == 0) return BigDecimal.ZERO;

        return BigDecimal.valueOf(numerador)
                .divide(BigDecimal.valueOf(denominador), 3, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularSlg(BattingAppearance ba) {
        if (ba.getAtBats() == null || ba.getAtBats() == 0) return BigDecimal.ZERO;

        int totalBases = (ba.getHits() != null ? ba.getHits() : 0)
                + (ba.getDoubles() != null ? ba.getDoubles() : 0)
                + (ba.getTriples() != null ? ba.getTriples() : 0) * 2
                + (ba.getHomeRuns() != null ? ba.getHomeRuns() : 0) * 3;

        return BigDecimal.valueOf(totalBases)
                .divide(BigDecimal.valueOf(ba.getAtBats()), 3, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularOps(BattingAppearance ba) {
        return calcularObp(ba).add(calcularSlg(ba));
    }

    private void procesarNodoPitcher(Game game, JsonNode teamBox, JsonNode playerNode, JsonNode stats) {
        try {
            int pitcherId = playerNode.path("person").path("id").asInt();
            String fullName = playerNode.path("person").path("fullName").asText();

            Pitcher pitcher = pitcherRepository.findById(pitcherId).orElse(
                    Pitcher.builder().id(pitcherId).fullName(fullName).build()
            );

            Integer teamId = teamBox.path("team").path("id").asInt();
            pitcher.setTeam(teamRepository.getReferenceById(teamId));

            boolean esAbridor = esAbridorDelEquipo(teamBox, pitcherId);
            pitcher.setPrimaryPosition(esAbridor ? "SP" : "RP");

            pitcher = pitcherRepository.save(pitcher);

            PitchingAppearance appearance = appearanceRepository
                    .findByPitcherIdAndGameId(pitcherId, game.getId())
                    .orElse(PitchingAppearance.builder().pitcher(pitcher).game(game).build());

            appearance.setStarter(esAbridor);

            String ipStr = getString(stats, "ip", "IP", "inningsPitched", "innings");
            appearance.setInningsPitched(parsearInnings(ipStr));

            int hits = getInt(stats, "h", "H", "hits");
            int runs = getInt(stats, "r", "R", "runs");
            int er = getInt(stats, "er", "ER", "earnedRuns", "earned_runs");
            int bb = getInt(stats, "bb", "BB", "walks", "baseOnBalls", "base_on_balls");
            int so = getInt(stats, "so", "SO", "strikeOuts", "strike_outs", "k");
            int hr = getInt(stats, "hr", "HR", "homeRuns", "home_runs");

            appearance.setHits(hits);
            appearance.setRuns(runs);
            appearance.setEarnedRuns(er);
            appearance.setBaseOnBalls(bb);
            appearance.setStrikeOuts(so);
            appearance.setHomeRuns(hr);

            int pitches = extraerLanzamientos(stats, pitcherId);
            appearance.setPitchesThrown(pitches);
            appearance.setGameDate(game.getOfficialDate());

            appearanceRepository.save(appearance);

        } catch (Exception e) {
            log.error("Error guardando pitcher en juego {}: {}", game.getId(), e.getMessage(), e);
        }
    }

    private void procesarNodoPitchersAntiguo(Game game, JsonNode teamBox, JsonNode pitchersNode) {
        Iterator<String> fieldNames = pitchersNode.fieldNames();
        boolean primero = true;

        while (fieldNames.hasNext()) {
            String pitcherIdStr = fieldNames.next();
            int pitcherId = Integer.parseInt(pitcherIdStr);
            JsonNode stats = pitchersNode.path(pitcherIdStr);

            Pitcher pitcher = pitcherRepository.findById(pitcherId).orElse(
                    Pitcher.builder().id(pitcherId).build()
            );

            Integer teamId = teamBox.path("team").path("id").asInt();
            pitcher.setTeam(teamRepository.getReferenceById(teamId));
            pitcher.setPrimaryPosition(primero ? "SP" : "RP");

            pitcher = pitcherRepository.save(pitcher);
            primero = false;

            PitchingAppearance appearance = appearanceRepository
                    .findByPitcherIdAndGameId(pitcherId, game.getId())
                    .orElse(PitchingAppearance.builder().pitcher(pitcher).game(game).build());

            appearance.setStarter("SP".equals(pitcher.getPrimaryPosition()));
            appearance.setInningsPitched(parsearInnings(getString(stats, "ip")));
            appearance.setHits(getInt(stats, "h"));
            appearance.setRuns(getInt(stats, "r"));
            appearance.setEarnedRuns(getInt(stats, "er"));
            appearance.setBaseOnBalls(getInt(stats, "bb"));
            appearance.setStrikeOuts(getInt(stats, "so"));
            appearance.setHomeRuns(getInt(stats, "hr"));
            appearance.setPitchesThrown(extraerLanzamientos(stats, pitcherId));
            appearance.setGameDate(game.getOfficialDate());

            appearanceRepository.save(appearance);
        }
    }

    private String getString(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.path(name);
            if (!value.isMissingNode() && !value.isNull()) {
                return value.asText();
            }
        }
        return "";
    }

    private int getInt(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.path(name);
            if (!value.isMissingNode() && value.isNumber()) {
                return value.asInt();
            }
        }
        return 0;
    }

    private int extraerLanzamientos(JsonNode stats, int pitcherId) {
        String[] campos = {"p", "pitches", "numberOfPitches", "np", "pitchCount"};
        for (String campo : campos) {
            JsonNode nodo = stats.path(campo);
            if (!nodo.isMissingNode() && nodo.isNumber()) {
                return nodo.asInt();
            }
        }
        return 0;
    }

    private BigDecimal parsearInnings(String ip) {
        if (ip == null || ip.isEmpty()) return BigDecimal.ZERO;

        try {
            String[] parts = ip.split("\\.");
            int entradas = Integer.parseInt(parts[0]);
            int outs = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            double innings = entradas + (outs / 3.0);
            return BigDecimal.valueOf(innings).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private boolean esAbridorDelEquipo(JsonNode teamBox, int pitcherId) {
        JsonNode pitchersNode = teamBox.path("pitchers");

        if (!pitchersNode.isMissingNode() && pitchersNode.isObject()) {
            Iterator<String> fieldNames = pitchersNode.fieldNames();
            if (fieldNames.hasNext()) {
                return Integer.parseInt(fieldNames.next()) == pitcherId;
            }
        }

        return false;
    }
}
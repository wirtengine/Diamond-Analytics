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

            // 🔹 FORMATO NUEVO
            JsonNode players = teamBox.path("players");

            if (!players.isMissingNode() && players.isObject()) {

                Iterator<Map.Entry<String, JsonNode>> playerFields = players.fields();

                while (playerFields.hasNext()) {

                    JsonNode playerNode = playerFields.next().getValue();
                    JsonNode stats = playerNode.path("stats").path("pitching");

                    // ✅ CAMBIO AQUÍ (NO BORRA NADA, SOLO MEJORA)
                    String pos = playerNode.path("position").path("abbreviation").asText();
                    boolean esPitcher = "P".equals(pos) || !stats.isMissingNode();
                    if (!esPitcher) continue;

                    procesarNodoPitcher(game, teamBox, playerNode, stats);
                }

            } else {
                // 🔹 FORMATO ANTIGUO
                JsonNode pitchersNode = teamBox.path("pitchers");

                if (!pitchersNode.isMissingNode()) {
                    procesarNodoPitchersAntiguo(game, teamBox, pitchersNode);
                }
            }
        }
    }

    // 🔥 TODO LO DEMÁS QUEDA EXACTAMENTE IGUAL ↓↓↓

    private void procesarNodoPitcher(Game game, JsonNode teamBox, JsonNode playerNode, JsonNode stats) {

        try {
            int pitcherId = playerNode.path("person").path("id").asInt();
            String fullName = playerNode.path("person").path("fullName").asText();

            if (log.isDebugEnabled()) {
                log.debug("📊 Stats recibidos para pitcher {} (juego {}): {}", pitcherId, game.getId(), stats.toString());
            }

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

            log.info("💾 {} | IP: {} | H: {} | ER: {} | BB: {} | SO: {} | P: {}",
                    fullName, ipStr, hits, er, bb, so, pitches);

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
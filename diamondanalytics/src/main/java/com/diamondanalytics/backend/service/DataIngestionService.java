package com.diamondanalytics.backend.service;

import com.diamondanalytics.backend.dto.odds.OddsApiResponse;
import com.diamondanalytics.backend.model.Game;
import com.diamondanalytics.backend.model.Odds;
import com.diamondanalytics.backend.model.Team;
import com.diamondanalytics.backend.repository.GameRepository;
import com.diamondanalytics.backend.repository.OddsRepository;
import com.diamondanalytics.backend.repository.TeamRepository;
import com.diamondanalytics.backend.service.external.OddsApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DataIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DataIngestionService.class);

    @Autowired
    private OddsApiClient oddsApiClient;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OddsRepository oddsRepository;

    @Transactional
    public void ingestGamesAndOdds() {
        log.info("Iniciando ingesta desde The Odds API");
        List<OddsApiResponse> oddsResponses = oddsApiClient.getMlbOdds();
        if (oddsResponses == null || oddsResponses.isEmpty()) {
            log.warn("No se obtuvieron datos de la API");
            return;
        }

        for (OddsApiResponse response : oddsResponses) {
            try {
                // 1. Obtener o crear equipos
                Team homeTeam = getOrCreateTeam(response.getHomeTeam());
                Team awayTeam = getOrCreateTeam(response.getAwayTeam());

                // 2. Obtener o crear juego
                Game game = gameRepository.findByExternalId(response.getId())
                        .orElse(new Game());
                game.setExternalId(response.getId());
                game.setHomeTeam(homeTeam);
                game.setAwayTeam(awayTeam);
                game.setStartTime(ZonedDateTime.parse(response.getCommenceTime()).toLocalDateTime());
                game.setStatus("scheduled");
                gameRepository.save(game);

                // 3. Guardar odds de cada casa de apuestas
                for (OddsApiResponse.Bookmaker bookmaker : response.getBookmakers()) {
                    Odds odds = new Odds();
                    odds.setGame(game);
                    odds.setBookmaker(bookmaker.getKey());
                    odds.setTimestamp(LocalDateTime.now());

                    for (OddsApiResponse.Market market : bookmaker.getMarkets()) {
                        if ("h2h".equals(market.getKey())) {
                            for (OddsApiResponse.Outcome outcome : market.getOutcomes()) {
                                if (outcome.getName().equalsIgnoreCase(homeTeam.getName())) {
                                    odds.setHomeMoneyline(outcome.getPrice());
                                } else if (outcome.getName().equalsIgnoreCase(awayTeam.getName())) {
                                    odds.setAwayMoneyline(outcome.getPrice());
                                }
                            }
                        }
                        // Puedes agregar spreads y totals si la API los devuelve
                    }
                    oddsRepository.save(odds);
                }
                log.debug("Juego guardado: {} vs {}", awayTeam.getName(), homeTeam.getName());
            } catch (Exception e) {
                log.error("Error procesando juego {}: {}", response.getId(), e.getMessage());
            }
        }
        log.info("Ingesta completada");
    }

    private Team getOrCreateTeam(String teamName) {
        return teamRepository.findByName(teamName)
                .orElseGet(() -> {
                    Team team = new Team();
                    team.setName(teamName);
                    team.setLeague("MLB");
                    team.setExternalId(teamName.replace(" ", "_").toLowerCase());
                    return teamRepository.save(team);
                });
    }
}
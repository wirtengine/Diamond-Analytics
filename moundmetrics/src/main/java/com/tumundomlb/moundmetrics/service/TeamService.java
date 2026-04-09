package com.tumundomlb.moundmetrics.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tumundomlb.moundmetrics.entity.Team;
import com.tumundomlb.moundmetrics.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final MlbApiClient mlbApiClient;
    private final TeamRepository teamRepository;

    @Transactional
    public void syncTeams() {
        JsonNode root = mlbApiClient.getTeams();
        JsonNode teamsNode = root.path("teams");
        if (teamsNode.isMissingNode() || !teamsNode.isArray()) {
            log.warn("No teams found in API response");
            return;
        }

        for (JsonNode teamNode : teamsNode) {
            if (teamNode.path("sport").path("id").asInt() != 1) continue;

            Integer id = teamNode.path("id").asInt();
            String name = teamNode.path("name").asText();
            String abbr = teamNode.path("abbreviation").asText();

            String leagueFull = teamNode.path("league").path("name").asText();
            // Mapear a abreviatura estándar
            String league = leagueFull.contains("American") ? "AL" : "NL";

            String divisionFull = teamNode.path("division").path("name").asText();
            String division = divisionFull;
            if (divisionFull.contains(" ")) {
                String[] parts = divisionFull.split(" ");
                division = parts[parts.length - 1];
            }

            Team team = teamRepository.findById(id)
                    .orElse(Team.builder().id(id).build());

            team.setName(name);
            team.setAbbreviation(abbr);
            team.setLeague(league);
            team.setDivision(division);

            teamRepository.save(team);
        }
        log.info("Synchronized {} teams", teamsNode.size());
    }
}
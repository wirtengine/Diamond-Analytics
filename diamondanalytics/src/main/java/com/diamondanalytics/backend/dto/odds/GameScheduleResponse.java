package com.diamondanalytics.backend.dto.clearsports;

import lombok.Data;
import java.util.List;

@Data
public class GameScheduleResponse {
    private List<GameEvent> events;

    @Data
    public static class GameEvent {
        private String id;
        private String status;
        private String startTime;
        private TeamInfo homeTeam;
        private TeamInfo awayTeam;
        private Score score;
    }

    @Data
    public static class TeamInfo {
        private String id;
        private String name;
        private String abbreviation;
    }

    @Data
    public static class Score {
        private Integer home;
        private Integer away;
    }
}
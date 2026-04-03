package com.diamondanalytics.backend.dto.odds;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class OddsApiResponse {
    private String id;
    @JsonProperty("sport_key")
    private String sportKey;
    @JsonProperty("sport_title")
    private String sportTitle;
    @JsonProperty("commence_time")
    private String commenceTime;
    @JsonProperty("home_team")
    private String homeTeam;
    @JsonProperty("away_team")
    private String awayTeam;
    private List<Bookmaker> bookmakers;

    @Data
    public static class Bookmaker {
        private String key;
        private String title;
        @JsonProperty("last_update")
        private String lastUpdate;
        private List<Market> markets;
    }

    @Data
    public static class Market {
        private String key;
        @JsonProperty("last_update")
        private String lastUpdate;
        private List<Outcome> outcomes;
    }

    @Data
    public static class Outcome {
        private String name;
        private Double price;
        private Double point;
    }
}
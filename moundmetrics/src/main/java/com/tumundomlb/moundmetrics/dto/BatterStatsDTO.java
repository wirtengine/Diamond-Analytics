package com.tumundomlb.moundmetrics.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class BatterStatsDTO {
    private Integer batterId;
    private String fullName;
    private int gamesPlayed;
    private int atBats;
    private int runs;
    private int hits;
    private int doubles;
    private int triples;
    private int homeRuns;
    private int rbi;
    private int baseOnBalls;
    private int strikeOuts;
    private int stolenBases;
    private BigDecimal avg;
    private BigDecimal obp;
    private BigDecimal slg;
    private BigDecimal ops;
}
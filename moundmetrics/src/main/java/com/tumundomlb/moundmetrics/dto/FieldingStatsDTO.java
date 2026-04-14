package com.tumundomlb.moundmetrics.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class FieldingStatsDTO {
    private Integer fielderId;
    private String fullName;
    private String primaryPosition;
    private int gamesPlayed;
    private int gamesStarted;
    private int putOuts;
    private int assists;
    private int errors;
    private int doublePlays;
    private int triplePlays;
    private BigDecimal fieldingPercentage; // (PO + A) / (PO + A + E)
}
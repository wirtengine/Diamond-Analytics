package com.tumundomlb.moundmetrics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PitchingAppearanceDto {
    private LocalDate gameDate;
    private Integer pitchesThrown;
    private BigDecimal inningsPitched;
}
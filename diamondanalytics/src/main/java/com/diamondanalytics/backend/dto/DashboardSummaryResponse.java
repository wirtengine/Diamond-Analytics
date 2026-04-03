package com.diamondanalytics.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalBets;
    private long wonBets;
    private long lostBets;
    private double roi;  // Retorno sobre inversión (porcentaje)
}
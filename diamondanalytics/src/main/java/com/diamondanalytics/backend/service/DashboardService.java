package com.diamondanalytics.backend.service;

import com.diamondanalytics.backend.dto.DashboardSummaryResponse;
import com.diamondanalytics.backend.model.User;

public interface DashboardService {
    DashboardSummaryResponse getSummaryForUser(User user);
}
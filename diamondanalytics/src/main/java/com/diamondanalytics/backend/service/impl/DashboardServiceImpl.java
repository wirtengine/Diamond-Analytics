package com.diamondanalytics.backend.service.impl;

import com.diamondanalytics.backend.dto.DashboardSummaryResponse;
import com.diamondanalytics.backend.model.BetResult;
import com.diamondanalytics.backend.model.User;
import com.diamondanalytics.backend.repository.BetRepository;
import com.diamondanalytics.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private BetRepository betRepository;

    @Override
    public DashboardSummaryResponse getSummaryForUser(User user) {
        long totalBets = betRepository.countByUser(user);
        long wonBets = betRepository.countByUserAndResult(user, BetResult.GANADA);
        long lostBets = betRepository.countByUserAndResult(user, BetResult.PERDIDA);

        double totalAmount = betRepository.sumAmountByUser(user);
        double totalProfit = betRepository.sumProfitByUser(user);

        double roi = 0.0;
        if (totalAmount > 0) {
            roi = (totalProfit / totalAmount) * 100;
        }

        return new DashboardSummaryResponse(totalBets, wonBets, lostBets, roi);
    }
}
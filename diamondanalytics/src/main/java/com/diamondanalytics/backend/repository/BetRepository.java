package com.diamondanalytics.backend.repository;

import com.diamondanalytics.backend.model.Bet;
import com.diamondanalytics.backend.model.BetResult;
import com.diamondanalytics.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BetRepository extends JpaRepository<Bet, Long> {

    long countByUser(User user);

    long countByUserAndResult(User user, BetResult result);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bet b WHERE b.user = :user")
    double sumAmountByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(b.profit), 0) FROM Bet b WHERE b.user = :user")
    double sumProfitByUser(@Param("user") User user);
}
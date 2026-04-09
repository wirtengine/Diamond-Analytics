package com.tumundomlb.moundmetrics.repository;

import com.tumundomlb.moundmetrics.entity.Pitcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PitcherRepository extends JpaRepository<Pitcher, Integer> {
    List<Pitcher> findByTeamId(Integer teamId);
}
package com.tumundomlb.moundmetrics.repository;

import com.tumundomlb.moundmetrics.entity.Batter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatterRepository extends JpaRepository<Batter, Integer> {
    List<Batter> findByTeamId(Integer teamId);
}
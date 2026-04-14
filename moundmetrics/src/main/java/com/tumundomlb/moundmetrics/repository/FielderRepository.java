package com.tumundomlb.moundmetrics.repository;

import com.tumundomlb.moundmetrics.entity.Fielder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FielderRepository extends JpaRepository<Fielder, Integer> {
    List<Fielder> findByTeamId(Integer teamId);
}
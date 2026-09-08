package com.example.app.repository;

import com.example.app.entity.PerformanceReviewCycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceReviewCycleRepository
    extends JpaRepository<PerformanceReviewCycle, Long> {}

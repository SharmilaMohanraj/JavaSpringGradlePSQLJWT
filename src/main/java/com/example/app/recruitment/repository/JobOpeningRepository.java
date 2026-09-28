package com.example.app.recruitment.repository;

import com.example.app.recruitment.JobOpening;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOpeningRepository extends JpaRepository<JobOpening, Long> {
  List<JobOpening> findByVacancyCountGreaterThan(int vacancyCount);
}

package com.example.app.recruitment.repository;

import com.example.app.recruitment.Candidate;
import com.example.app.recruitment.CandidateStage;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
  @Query("select c.jobOpening.id as jobOpeningId, c.stage as stage, count(c) as count "
      + "from Candidate c where c.jobOpening.id in :openingIds "
      + "group by c.jobOpening.id, c.stage")
  List<CandidateStageCount> countByJobOpeningIds(@Param("openingIds") Collection<Long> openingIds);

  interface CandidateStageCount {
    Long getJobOpeningId();
    CandidateStage getStage();
    long getCount();
  }
}

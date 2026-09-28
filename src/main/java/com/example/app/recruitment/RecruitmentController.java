package com.example.app.recruitment;

import com.example.app.recruitment.dto.RecruitmentDtos.CandidateResponse;
import com.example.app.recruitment.dto.RecruitmentDtos.CreateCandidateRequest;
import com.example.app.recruitment.dto.RecruitmentDtos.CreateJobOpeningRequest;
import com.example.app.recruitment.dto.RecruitmentDtos.JobOpeningSummaryResponse;
import com.example.app.recruitment.dto.RecruitmentDtos.UpdateCandidateStageRequest;
import com.example.app.recruitment.service.RecruitmentService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recruitment")
public class RecruitmentController {
  private final RecruitmentService recruitmentService;

  public RecruitmentController(RecruitmentService recruitmentService) {
    this.recruitmentService = recruitmentService;
  }

  @PostMapping("/job-openings")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<JobOpeningSummaryResponse> createJobOpening(
      @Valid @RequestBody CreateJobOpeningRequest request) {
    return ResponseEntity.status(201).body(recruitmentService.createJobOpening(request));
  }

  @PostMapping("/candidates")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<CandidateResponse> createCandidate(
      @Valid @RequestBody CreateCandidateRequest request) {
    return ResponseEntity.status(201).body(recruitmentService.createCandidate(request));
  }

  @PatchMapping("/candidates/{candidateId}/stage")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<CandidateResponse> updateCandidateStage(@PathVariable Long candidateId,
      @Valid @RequestBody UpdateCandidateStageRequest request) {
    return ResponseEntity.ok(recruitmentService.updateCandidateStage(candidateId, request.stage));
  }

  @ExceptionHandler(RecruitmentNotFoundException.class)
  public ResponseEntity<Void> handleNotFound(RecruitmentNotFoundException exception) {
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/job-openings/open")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<List<JobOpeningSummaryResponse>> getOpenJobOpenings() {
    return ResponseEntity.ok(recruitmentService.getOpenJobOpenings());
  }
}

package com.example.app.recruitment.service;

import com.example.app.entity.Department;
import com.example.app.entity.Designation;
import com.example.app.repository.DepartmentRepository;
import com.example.app.repository.DesignationRepository;
import com.example.app.recruitment.Candidate;
import com.example.app.recruitment.CandidateStage;
import com.example.app.recruitment.JobOpening;
import com.example.app.recruitment.RecruitmentNotFoundException;
import com.example.app.recruitment.dto.RecruitmentDtos.CandidateResponse;
import com.example.app.recruitment.dto.RecruitmentDtos.CreateCandidateRequest;
import com.example.app.recruitment.dto.RecruitmentDtos.CreateJobOpeningRequest;
import com.example.app.recruitment.dto.RecruitmentDtos.JobOpeningSummaryResponse;
import com.example.app.recruitment.repository.CandidateRepository;
import com.example.app.recruitment.repository.CandidateRepository.CandidateStageCount;
import com.example.app.recruitment.repository.JobOpeningRepository;
import com.example.app.service.EmployeeService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecruitmentService {
  private final JobOpeningRepository jobOpeningRepository;
  private final CandidateRepository candidateRepository;
  private final DepartmentRepository departmentRepository;
  private final DesignationRepository designationRepository;
  private final EmployeeService employeeService;

  public RecruitmentService(JobOpeningRepository jobOpeningRepository,
      CandidateRepository candidateRepository, DepartmentRepository departmentRepository,
      DesignationRepository designationRepository, EmployeeService employeeService) {
    this.jobOpeningRepository = jobOpeningRepository;
    this.candidateRepository = candidateRepository;
    this.departmentRepository = departmentRepository;
    this.designationRepository = designationRepository;
    this.employeeService = employeeService;
  }

  @Transactional
  public JobOpeningSummaryResponse createJobOpening(CreateJobOpeningRequest request) {
    Department department = departmentRepository.findById(request.departmentId)
        .orElseThrow(() -> new RecruitmentNotFoundException("Department not found: " + request.departmentId));
    Designation designation = designationRepository.findById(request.designationId)
        .orElseThrow(() -> new RecruitmentNotFoundException("Designation not found: " + request.designationId));
    JobOpening opening = new JobOpening();
    opening.setTitle(request.title);
    opening.setDescription(request.description);
    opening.setVacancyCount(request.vacancyCount);
    opening.setDepartment(department);
    opening.setDesignation(designation);
    return toSummary(jobOpeningRepository.save(opening), stageCounts(null));
  }

  @Transactional
  public CandidateResponse createCandidate(CreateCandidateRequest request) {
    JobOpening opening = jobOpeningRepository.findById(request.jobOpeningId)
        .orElseThrow(() -> new RecruitmentNotFoundException("Job opening not found: " + request.jobOpeningId));
    Candidate candidate = new Candidate();
    candidate.setJobOpening(opening);
    candidate.setName(request.name);
    candidate.setEmail(request.email);
    candidate.setResumeLink(request.resumeLink);
    candidate.setStage(CandidateStage.APPLIED);
    return toResponse(candidateRepository.save(candidate));
  }

  @Transactional
  public CandidateResponse updateCandidateStage(Long candidateId, CandidateStage requestedStage) {
    Candidate candidate = candidateRepository.findById(candidateId)
        .orElseThrow(() -> new RecruitmentNotFoundException("Candidate not found: " + candidateId));
    if (candidate.getStage() == CandidateStage.HIRED && requestedStage == CandidateStage.HIRED) {
      return toResponse(candidate);
    }
    if (candidate.getStage() != CandidateStage.HIRED && requestedStage == CandidateStage.HIRED) {
      JobOpening opening = candidate.getJobOpening();
      if (opening.getVacancyCount() == null || opening.getVacancyCount() <= 0) {
        throw new IllegalArgumentException("Job opening has no remaining vacancies");
      }
      employeeService.provisionEmployee(candidate.getName(), candidate.getEmail());
      opening.setVacancyCount(opening.getVacancyCount() - 1);
    }
    candidate.setStage(requestedStage);
    return toResponse(candidateRepository.save(candidate));
  }

  @Transactional(readOnly = true)
  public List<JobOpeningSummaryResponse> getOpenJobOpenings() {
    List<JobOpening> openings = jobOpeningRepository.findByVacancyCountGreaterThan(0);
    if (openings.isEmpty()) return new ArrayList<>();
    List<Long> openingIds = openings.stream().map(JobOpening::getId).collect(Collectors.toList());
    Map<Long, List<CandidateStageCount>> countsByOpening = candidateRepository
        .countByJobOpeningIds(openingIds).stream()
        .collect(Collectors.groupingBy(CandidateStageCount::getJobOpeningId));
    return openings.stream()
        .map(opening -> toSummary(opening, stageCounts(countsByOpening.get(opening.getId()))))
        .collect(Collectors.toList());
  }

  private EnumMap<CandidateStage, Long> stageCounts(Collection<CandidateStageCount> counts) {
    EnumMap<CandidateStage, Long> stageCounts = new EnumMap<>(CandidateStage.class);
    for (CandidateStage stage : CandidateStage.values()) stageCounts.put(stage, 0L);
    if (counts != null) {
      for (CandidateStageCount count : counts) stageCounts.put(count.getStage(), count.getCount());
    }
    return stageCounts;
  }

  private CandidateResponse toResponse(Candidate candidate) {
    return new CandidateResponse(candidate.getId(), candidate.getJobOpening().getId(), candidate.getName(),
        candidate.getEmail(), candidate.getResumeLink(), candidate.getStage());
  }

  private JobOpeningSummaryResponse toSummary(JobOpening opening,
      Map<CandidateStage, Long> candidateStageCounts) {
    return new JobOpeningSummaryResponse(opening.getId(), opening.getTitle(), opening.getDescription(),
        opening.getVacancyCount(), opening.getDepartment().getId(), opening.getDepartment().getName(),
        opening.getDesignation().getId(), opening.getDesignation().getTitle(), candidateStageCounts);
  }
}

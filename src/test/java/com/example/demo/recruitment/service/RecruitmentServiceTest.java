package com.example.demo.recruitment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.app.entity.Department;
import com.example.app.entity.Designation;
import com.example.app.repository.DepartmentRepository;
import com.example.app.repository.DesignationRepository;
import com.example.app.recruitment.Candidate;
import com.example.app.recruitment.CandidateStage;
import com.example.app.recruitment.JobOpening;
import com.example.app.recruitment.RecruitmentNotFoundException;
import com.example.app.recruitment.dto.RecruitmentDtos.CreateCandidateRequest;
import com.example.app.recruitment.repository.CandidateRepository;
import com.example.app.recruitment.repository.JobOpeningRepository;
import com.example.app.recruitment.service.RecruitmentService;
import com.example.app.service.EmployeeService;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {
  @Mock private JobOpeningRepository jobOpenings;
  @Mock private CandidateRepository candidates;
  @Mock private DepartmentRepository departments;
  @Mock private DesignationRepository designations;
  @Mock private EmployeeService employees;
  private RecruitmentService service;

  @BeforeEach
  void setUp() {
    service = new RecruitmentService(jobOpenings, candidates, departments, designations, employees);
  }

  @Test
  void candidateCreationDefaultsToApplied() {
    JobOpening opening = opening(10L, 2);
    when(jobOpenings.findById(10L)).thenReturn(Optional.of(opening));
    when(candidates.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.createCandidate(new CreateCandidateRequest(10L, "Ada", "ada@example.com", "https://resume"));

    ArgumentCaptor<Candidate> captor = ArgumentCaptor.forClass(Candidate.class);
    verify(candidates).save(captor.capture());
    assertEquals(CandidateStage.APPLIED, captor.getValue().getStage());
  }

  @Test
  void hiringProvisionsOnceAndDecrementsVacancy() {
    JobOpening opening = opening(10L, 1);
    Candidate candidate = candidate(opening, CandidateStage.APPLIED);
    when(candidates.findById(4L)).thenReturn(Optional.of(candidate));
    when(candidates.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.updateCandidateStage(4L, CandidateStage.HIRED);

    assertEquals(CandidateStage.HIRED, candidate.getStage());
    assertEquals(0, opening.getVacancyCount());
    verify(employees).provisionEmployee("Ada", "ada@example.com");
  }

  @Test
  void hiringWithZeroVacancyDoesNotProvisionOrGoNegative() {
    JobOpening opening = opening(10L, 0);
    Candidate candidate = candidate(opening, CandidateStage.APPLIED);
    when(candidates.findById(4L)).thenReturn(Optional.of(candidate));

    assertThrows(IllegalArgumentException.class, () -> service.updateCandidateStage(4L, CandidateStage.HIRED));

    assertEquals(0, opening.getVacancyCount());
    verify(employees, never()).provisionEmployee(any(), any());
    verify(candidates, never()).save(any());
  }

  @Test
  void repeatHireIsIdempotent() {
    JobOpening opening = opening(10L, 1);
    Candidate candidate = candidate(opening, CandidateStage.HIRED);
    when(candidates.findById(4L)).thenReturn(Optional.of(candidate));

    service.updateCandidateStage(4L, CandidateStage.HIRED);

    assertEquals(1, opening.getVacancyCount());
    verify(employees, never()).provisionEmployee(any(), any());
    verify(candidates, never()).save(any());
  }

  @Test
  void openJobsHaveZeroFilledCountsForEveryStage() {
    JobOpening opening = opening(10L, 2);
    when(jobOpenings.findByVacancyCountGreaterThan(0)).thenReturn(Collections.singletonList(opening));
    when(candidates.countByJobOpeningIds(Collections.singletonList(10L))).thenReturn(Arrays.asList(
        count(10L, CandidateStage.APPLIED, 3), count(10L, CandidateStage.INTERVIEW, 1)));

    var response = service.getOpenJobOpenings().get(0);

    assertEquals(3L, response.stageCounts.get(CandidateStage.APPLIED));
    assertEquals(1L, response.stageCounts.get(CandidateStage.INTERVIEW));
    assertEquals(0L, response.stageCounts.get(CandidateStage.HIRED));
    assertEquals(CandidateStage.values().length, response.stageCounts.size());
  }

  @Test
  void missingOpeningAndCandidateHaveClearNotFoundBehavior() {
    when(jobOpenings.findById(anyLong())).thenReturn(Optional.empty());
    when(candidates.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(RecruitmentNotFoundException.class,
        () -> service.createCandidate(new CreateCandidateRequest(8L, "Ada", "ada@example.com", "link")));
    assertThrows(RecruitmentNotFoundException.class,
        () -> service.updateCandidateStage(9L, CandidateStage.SCREENING));
  }

  private JobOpening opening(Long id, int vacancies) {
    Department department = new Department();
    department.setName("Engineering");
    ReflectionTestUtils.setField(department, "id", 1L);
    Designation designation = new Designation();
    designation.setTitle("Developer");
    ReflectionTestUtils.setField(designation, "id", 2L);
    JobOpening opening = new JobOpening();
    ReflectionTestUtils.setField(opening, "id", id);
    opening.setTitle("Backend developer");
    opening.setDescription("Build services");
    opening.setVacancyCount(vacancies);
    opening.setDepartment(department);
    opening.setDesignation(designation);
    return opening;
  }

  private Candidate candidate(JobOpening opening, CandidateStage stage) {
    Candidate candidate = new Candidate();
    candidate.setName("Ada");
    candidate.setEmail("ada@example.com");
    candidate.setResumeLink("https://resume");
    candidate.setJobOpening(opening);
    candidate.setStage(stage);
    return candidate;
  }

  private CandidateRepository.CandidateStageCount count(Long openingId, CandidateStage stage, long value) {
    return new CandidateRepository.CandidateStageCount() {
      public Long getJobOpeningId() { return openingId; }
      public CandidateStage getStage() { return stage; }
      public long getCount() { return value; }
    };
  }
}

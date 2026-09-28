package com.example.demo.recruitment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.app.recruitment.CandidateStage;
import com.example.app.recruitment.RecruitmentController;
import com.example.app.recruitment.RecruitmentNotFoundException;
import com.example.app.recruitment.dto.RecruitmentDtos.CandidateResponse;
import com.example.app.recruitment.dto.RecruitmentDtos.CreateCandidateRequest;
import com.example.app.recruitment.dto.RecruitmentDtos.CreateJobOpeningRequest;
import com.example.app.recruitment.dto.RecruitmentDtos.JobOpeningSummaryResponse;
import com.example.app.recruitment.service.RecruitmentService;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RecruitmentControllerTest {
  @Mock private RecruitmentService recruitmentService;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new RecruitmentController(recruitmentService)).build();
  }

  @Test
  void missingJobOpeningReturnsNotFound() throws Exception {
    when(recruitmentService.createCandidate(any(CreateCandidateRequest.class)))
        .thenThrow(new RecruitmentNotFoundException("Job opening not found: 99"));

    mockMvc.perform(post("/api/recruitment/candidates")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"jobOpeningId\":99,\"name\":\"Ada\",\"email\":\"ada@example.com\",\"resumeLink\":\"https://resume\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void openingSummaryUsesStageCountsJsonProperty() throws Exception {
    Map<CandidateStage, Long> stageCounts = new EnumMap<>(CandidateStage.class);
    stageCounts.put(CandidateStage.APPLIED, 1L);
    stageCounts.put(CandidateStage.SCREENING, 0L);
    stageCounts.put(CandidateStage.INTERVIEW, 0L);
    stageCounts.put(CandidateStage.OFFERED, 0L);
    stageCounts.put(CandidateStage.HIRED, 0L);
    stageCounts.put(CandidateStage.REJECTED, 0L);
    when(recruitmentService.createJobOpening(any(CreateJobOpeningRequest.class))).thenReturn(
        new JobOpeningSummaryResponse(1L, "Backend Developer", "Build services", 2,
            7L, "Engineering", 8L, "Developer", stageCounts));

    mockMvc.perform(post("/api/recruitment/job-openings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"Backend Developer\",\"description\":\"Build services\",\"vacancyCount\":2,\"departmentId\":7,\"designationId\":8}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.stageCounts.APPLIED").value(1))
        .andExpect(jsonPath("$.candidateStageCounts").doesNotExist());
  }
}

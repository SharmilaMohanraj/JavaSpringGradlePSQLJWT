package com.example.app.recruitment.dto;

import com.example.app.recruitment.CandidateStage;
import java.util.Map;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public final class RecruitmentDtos {
  private RecruitmentDtos() {}

  public static class CreateJobOpeningRequest {
    @NotBlank public String title;
    @NotBlank public String description;
    @NotNull @Positive public Integer vacancyCount;
    @NotNull @Positive public Long departmentId;
    @NotNull @Positive public Long designationId;

    public CreateJobOpeningRequest() {}
    public CreateJobOpeningRequest(String title, String description, Integer vacancyCount,
        Long departmentId, Long designationId) {
      this.title = title;
      this.description = description;
      this.vacancyCount = vacancyCount;
      this.departmentId = departmentId;
      this.designationId = designationId;
    }
  }

  public static class CreateCandidateRequest {
    @NotNull @Positive public Long jobOpeningId;
    @NotBlank public String name;
    @NotBlank @Email public String email;
    @NotBlank public String resumeLink;

    public CreateCandidateRequest() {}
    public CreateCandidateRequest(Long jobOpeningId, String name, String email, String resumeLink) {
      this.jobOpeningId = jobOpeningId;
      this.name = name;
      this.email = email;
      this.resumeLink = resumeLink;
    }
  }

  public static class UpdateCandidateStageRequest {
    @NotNull public CandidateStage stage;

    public UpdateCandidateStageRequest() {}
    public UpdateCandidateStageRequest(CandidateStage stage) { this.stage = stage; }
  }

  public static class CandidateResponse {
    public Long id;
    public Long jobOpeningId;
    public String name;
    public String email;
    public String resumeLink;
    public CandidateStage stage;

    public CandidateResponse() {}
    public CandidateResponse(Long id, Long jobOpeningId, String name, String email,
        String resumeLink, CandidateStage stage) {
      this.id = id;
      this.jobOpeningId = jobOpeningId;
      this.name = name;
      this.email = email;
      this.resumeLink = resumeLink;
      this.stage = stage;
    }
  }

  public static class JobOpeningSummaryResponse {
    public Long id;
    public String title;
    public String description;
    public Integer vacancyCount;
    public Long departmentId;
    public String departmentName;
    public Long designationId;
    public String designationTitle;
    public Map<CandidateStage, Long> stageCounts;

    public JobOpeningSummaryResponse() {}
    public JobOpeningSummaryResponse(Long id, String title, String description, Integer vacancyCount,
        Long departmentId, String departmentName, Long designationId, String designationTitle,
        Map<CandidateStage, Long> stageCounts) {
      this.id = id;
      this.title = title;
      this.description = description;
      this.vacancyCount = vacancyCount;
      this.departmentId = departmentId;
      this.departmentName = departmentName;
      this.designationId = designationId;
      this.designationTitle = designationTitle;
      this.stageCounts = stageCounts;
    }
  }
}

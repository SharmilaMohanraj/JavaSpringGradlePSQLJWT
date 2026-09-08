package com.example.app.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public final class HrDtos {
  private HrDtos() {}

  public static class DepartmentDto {
    @NotBlank public String name;
  }

  public static class DesignationDto {
    @NotBlank public String title;
  }

  public static class EmployeeDto {
    @NotBlank public String name;
    @NotBlank public String email;
  }

  public static class LeaveRequestDto {
    @Positive public Long employeeId;
    @Positive public Integer days;
    @NotBlank public String reason;
  }

  public static class PayrollRecordDto {
    @Positive public Long employeeId;
    @NotBlank public String period;
  }

  public static class PerformanceReviewDto {
    @Positive public Long employeeId;
    @NotBlank public String cycleName;
  }

  public static class HierarchyDto {
    @Positive public Long managerId;
    @Positive public Long subordinateId;
  }
}
